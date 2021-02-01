package net.mortalsilence

import io.quarkus.runtime.QuarkusApplication
import io.quarkus.runtime.annotations.QuarkusMain
import net.mortalsilence.lastfm.LovedTracksRetriever
import net.mortalsilence.spotify.PlaylistCreator
import javax.inject.Inject
import javax.sound.midi.Sequence

//@QuarkusMain
//class Main(): QuarkusApplication {
//
//    @Inject
//    private lateinit var playlistCreator: PlaylistCreator
//    @Inject
//    private lateinit var lovedTracksRetriever: LovedTracksRetriever
//
//    override fun run(vararg args: String?): Int {
//        playlistCreator.createPlaylist(/*lovedTracksRetriever.retrieveLovedTracks()*/sequenceOf(TrackInfo("Tool", "Lateralus", "Shism"), TrackInfo("Hella", "Hold Your Horse Is", "Biblical Violence")))
//        return 10
//    }
//}