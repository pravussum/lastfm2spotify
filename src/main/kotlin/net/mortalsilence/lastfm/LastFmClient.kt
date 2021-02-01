package net.mortalsilence.lastfm

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import java.util.concurrent.CompletionStage
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam

@Path("2.0/")
@RegisterRestClient
interface LastFmClient {

    @GET
    @Path("/")
    @Produces("application/json")
    fun get(@QueryParam("method") method: String, @QueryParam("user") user: String,
            @QueryParam("api_key") apiKey: String, @QueryParam("format") format: String,
            @QueryParam("page") page: Int, @QueryParam("extended") extended: String = "0",
            @QueryParam("limit") limit: Int = 1): String


    @GET
    @Path("/")
    @Produces("application/json")
    fun getAsync(@QueryParam("method") method: String, @QueryParam("user") user: String,
            @QueryParam("api_key") apiKey: String, @QueryParam("format") format: String,
            @QueryParam("page") page: Int, @QueryParam("extended") extended: String = "0",
            @QueryParam("limit") limit: Int = 1): CompletionStage<String>
}