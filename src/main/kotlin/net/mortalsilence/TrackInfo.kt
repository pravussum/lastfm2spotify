package net.mortalsilence

data class TrackInfo(val artist: String, val album: String, val track: String){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrackInfo

        if (artist != other.artist) return false
        if (album != other.album) return false
        if (track != other.track) return false

        return true
    }

    override fun hashCode(): Int {
        var result = artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + track.hashCode()
        return result
    }
}

