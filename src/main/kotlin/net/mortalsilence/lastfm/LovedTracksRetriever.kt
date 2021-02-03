package net.mortalsilence.lastfm

import com.jayway.jsonpath.JsonPath
import net.mortalsilence.TrackInfo
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.util.concurrent.CompletableFuture
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class LovedTracksRetriever {

    @ConfigProperty(name="lastfm.apikey")
    lateinit var apikey: String

    @Inject @RestClient lateinit var lastFmClient: LastFmClient

    fun retrieveLovedTracks(user: String): Set<TrackInfo> {
        return getAllLovedTracks(user)
    }

    private fun getTracksForPage(page: String): Pair<Int, Sequence<TrackInfo>> {
        val ctx = JsonPath.parse(page)
        val totalPages = ctx.read("$.recenttracks['@attr'].totalPages", Integer::class.java).toInt()
        val tracks = ctx
            .read("$.recenttracks.track[?(@.loved==1)]", List::class.java)
            .asSequence()
            .map {
                val trackCtx = JsonPath.parse(it)
                TrackInfo(
                    trackCtx.read("$.artist.name", String::class.java),
                    trackCtx.read("$.album.#text", String::class.java),
                    trackCtx.read("$.name", String::class.java)
                )
            }
        return Pair(totalPages, tracks)
    }

    fun getAllLovedTracks(user: String) : Set<TrackInfo> {
        val firstPage = lastFmClient.get("user.getrecenttracks", user, apikey, "json", 1, "1", 200)
        val (total, tracks) = getTracksForPage(firstPage)

        if(total <= 1) {
            return tracks.toSet()
        }
        val requests: MutableSet<CompletableFuture<Sequence<TrackInfo>>> = HashSet()
        for(i in 2..total) {
                requests.add(
                    lastFmClient.getAsync("user.getrecenttracks", user, apikey, "json", i, "1", 200)
                    .thenApply { s -> getTracksForPage(s).second }
                    .toCompletableFuture()
                )
            }
            return CompletableFuture.allOf(*requests.toTypedArray())
                .thenApply<Set<TrackInfo>> {
                    requests.flatMap { f -> f.join().toSet() }
                        .toSet()
                }.join()
    }
}