package net.mortalsilence.spotify

import org.eclipse.microprofile.config.inject.ConfigProperty
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.exceptions.detailed.TooManyRequestsException
import com.wrapper.spotify.model_objects.specification.Track
import net.mortalsilence.TrackInfo
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class PlaylistCreator {

    @ConfigProperty(name="spotify.clientid")
    lateinit var apikey: String

    @ConfigProperty(name="spotify.clientsecret")
    lateinit var apisecret: String

    @Inject
    lateinit var spotifyApiAccess: SpotifyApiAccess

    fun createPlaylist(retrieveLovedTracks: Set<TrackInfo>) {

        val spotifyApi = spotifyApiAccess.getApiForPrivateAccess()
        val playlist = spotifyApi.createPlaylist("pravussum", "Testplaylist").build()
            .execute()

        val spotifyTrackIds = retrieveLovedTracks.map {
            println(it.artist + " - " + it.album + " - " + it.track)
            performTrackSearch(spotifyApi, it)
        }
        .filterNotNull()
        .map { it.uri }

        spotifyApi.addItemsToPlaylist(playlist.id, spotifyTrackIds.toSet().toTypedArray()).build().execute()
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