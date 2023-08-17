package blekel.giphy.data

import blekel.giphy.data.util.json
import okhttp3.OkHttpClient
import okhttp3.Request

class GiphyService {

    private val searchUrl = "https://api.giphy.com/v1/gifs/search"
    private val apiKey = "xeuAXLNQEeSlPoABiHRH0r7mmPIUnlD9"
    private val httpClient = OkHttpClient()

    fun search(query: String): SearchResponse? {
        if (query.isBlank()) {
            return null
        }
        val url = "$searchUrl?api_key=$apiKey&q=$query"
        val request = Request.Builder().get().url(url).build()
        val call = httpClient.newCall(request)
        val response = call.execute()

        val body = response.body?.string()
            ?: return null
        return json.decodeFromString(body)
    }
}
