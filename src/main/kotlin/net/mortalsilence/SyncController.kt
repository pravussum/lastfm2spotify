package net.mortalsilence

import net.mortalsilence.lastfm.LovedTracksRetriever
import net.mortalsilence.spotify.PlaylistCreator
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

@Path("")
class SyncController {

    @Inject
    lateinit var lovedTracksRetriever: LovedTracksRetriever

    @Inject
    lateinit var playlistCreator: PlaylistCreator

    @GET
    @Path("/sync/{lastfmuser}")
    fun startSync(@QueryParam("lastfmuser") lastfmuser: String?) {
        val lovedTracks = lovedTracksRetriever.retrieveLovedTracks()
        playlistCreator.createPlaylist(lovedTracks)
    }


}