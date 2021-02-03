package net.mortalsilence.spotify

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.detailed.TooManyRequestsException
import com.wrapper.spotify.model_objects.specification.Track
import net.mortalsilence.TrackInfo
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class PlaylistCreator {

    @Inject
    lateinit var spotifyApiAccess: SpotifyApiAccess

    fun createPlaylist(spotifyUser: String, retrieveLovedTracks: Set<TrackInfo>) {

        val spotifyApi = spotifyApiAccess.getApiForPrivateAccess()
        val playlist = spotifyApi.createPlaylist(spotifyUser, "❤ Last.fm favourites " + DateTimeFormatter.ISO_DATE.format(LocalDate.now()))
            .build()
            .execute()

        val spotifyTrackIds = retrieveLovedTracks.map {
            println(it.artist + " - " + it.album + " - " + it.track)
            performTrackSearch(spotifyApi, it)
        }
        .filterNotNull()
        .map<Track, String?> { it.uri }
        .toSet()

        if(spotifyTrackIds.size > 10_000) {
            println("Playlist must not exceed 10.000 items. Ignoring exceeding items.")
        }

        spotifyTrackIds
            .chunked(10_0000)
            .first()        // only 10.000 elements allowed in a playlist
            .chunked(50)      // only 100 items per request allowed
            .forEach {
                spotifyApi.addItemsToPlaylist(playlist.id, it.toTypedArray()).build().execute()
            }
    }

     private fun performTrackSearch(spotifyApi: SpotifyApi, trackInfo: TrackInfo): Track? {
        try {
            val albumCondition = if(trackInfo.album.isNotBlank()) " album:${trackInfo.album}" else ""
            val query = "${trackInfo.track} artist:${trackInfo.artist}$albumCondition"
            val searchResult = spotifyApi
                .searchTracks(query)
                .build()
                .execute()
            if (searchResult.items.isEmpty()) {
                println("Didn't find Spotify search result for $query")
                if(trackInfo.album.isNotBlank()) {
                    println("Retrying without album...")
                    return performTrackSearch(spotifyApi, TrackInfo(trackInfo.artist, "", trackInfo.track))
                }
            }
            return searchResult.items.firstOrNull()
        } catch (e: TooManyRequestsException) {
            println("Rate limit hit. Waiting ${e.retryAfter} seconds before retrying")
            Thread.sleep(e.retryAfter * 1000L)
            return performTrackSearch(spotifyApi, trackInfo)
        }
    }
}