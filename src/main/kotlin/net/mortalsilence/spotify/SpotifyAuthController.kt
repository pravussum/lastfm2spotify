package net.mortalsilence.spotify

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.net.URI
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@Path("")
class SpotifyAuthController {

    @ConfigProperty(name="spotify.clientid")
    lateinit var clientId: String

    @ConfigProperty(name="spotify.clientsecret")
    lateinit var clientSecret: String

    @Inject
    lateinit var spotifyApiAccess: SpotifyApiAccess

    @GET
    @Path("/start")
    @Produces(MediaType.TEXT_PLAIN)
    fun startAuthFlow(@Context uriInfo: UriInfo) : Response {

        val redirectUri = getRedirectUri(uriInfo)
        val spotifyApi = SpotifyApi.Builder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRedirectUri(redirectUri)
            .build()

        val authCodeUri = spotifyApi.authorizationCodeUri()
            .scope("playlist-read-private,playlist-modify-private,playlist-modify-public")
            .build().execute()

        println(authCodeUri)

        return Response.temporaryRedirect(authCodeUri).build()
    }

    private fun getRedirectUri(uriInfo: UriInfo) =
        SpotifyHttpManager.makeUri(uriInfo.baseUri.toString() + "spotify-auth-callback")

    @GET
    @Path("/spotify-auth-callback")

    fun authCallback(@QueryParam("code") code: String, @Context uriInfo: UriInfo): Response {
        // TODO verify state
        spotifyApiAccess.setCode(code, getRedirectUri(uriInfo))
        return Response.temporaryRedirect(URI("/authorized")).build()
    }

    @GET
    @Path("/authorized")
    fun isAuthorized() : Boolean {
        return spotifyApiAccess.isAuthorized()
    }
}