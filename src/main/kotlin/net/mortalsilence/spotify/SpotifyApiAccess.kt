package net.mortalsilence.spotify

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.credentials.ClientCredentials
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.net.URI
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SpotifyApiAccess {
    @ConfigProperty(name="spotify.clientid")
    lateinit var apikey: String

    @ConfigProperty(name="spotify.clientsecret")
    lateinit var apisecret: String

    lateinit var spotifyApi: SpotifyApi
    lateinit var clientCredentials: ClientCredentials

    @PostConstruct
    private fun postConstruct() {
        spotifyApi = SpotifyApi.Builder()
            .setClientId(apikey)
            .setClientSecret(apisecret)
            .build()
        clientCredentials = spotifyApi.clientCredentials().build().execute()
    }

    fun getApiForPublicAccess(): SpotifyApi {
        spotifyApi.accessToken = clientCredentials.accessToken
        return spotifyApi
    }

    fun getApiForPrivateAccess(): SpotifyApi {
        // TODO start auth flow
        return spotifyApi
    }

    fun setCode(authCode: String, redirectUri: URI) {
        val authCodeCredentials =SpotifyApi.Builder()
            .setClientId(apikey)
            .setClientSecret(apisecret)
            .setRedirectUri(redirectUri)
            .build()
            .authorizationCode(authCode)
            .build().execute()
        spotifyApi.accessToken = authCodeCredentials.accessToken
        spotifyApi.refreshToken = authCodeCredentials.refreshToken
    }

    fun isAuthorized(): Boolean {
        println(spotifyApi.accessToken)
        return spotifyApi.accessToken != null
    }

}