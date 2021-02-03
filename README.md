# lastfm2spotify project

## Get developer accounts 
Extra simple throw-away importer for your last.fm loved favourites as a Spotify playlist.
You need Last.fm as well as Spotify developer accounts.

* https://developer.spotify.com/dashboard/
* https://www.last.fm/api/account/create


## Adapt configuration

Add the corresponding credentials in application.properties
-Dspotify.clientid=<Spotify client id> -Dspotify.clientsecret=<Spotify client secret> -Dlastfm.apikey=<last.fm API key>


## Import/Export

Fire up http://localhost:8080/start in a brower to start the Spotify authentication. You will be redirected to the Spotify authentication service where you'll need to approve that the app is allowed to access your Spotify data. 
Once this is done, you will be redirected to the app (it should report "true" if authentication flow was finished successfully).

Start the import by visiting (replace <last.fm username> and <Spotify username> obviously)
http://localhost:8080/sync?lastfmuser=<last.fm username>&spotifyuser=<Spotify username> e.g.
`http://localhost:8080/sync?lastfmuser=sherlock&spotifyuser=watson`

## Notes

Be patient:
* Due to the fact that last.fm doesn't offer an API method to retrieve all loved tracks ALL tracks need to be scraped page by page.
* Due to the fact that Spotify doesn't offer an API method for searching multiple tracks at once or adding items to playlists by name, we have to search track by track.

The last.fm request will be sent in parallel. 
Spotify has a rate limit in place, which will kick in from time to time (already does when importing a few hundred tracks). Requests will be throttled automatically.   

## TBD

A little front end ;-)