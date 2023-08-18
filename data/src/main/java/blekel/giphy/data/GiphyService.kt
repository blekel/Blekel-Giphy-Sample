package blekel.giphy.data

import blekel.giphy.data.util.json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

class GiphyService {

    private val searchUrl = "https://api.giphy.com/v1/gifs/search"
    private val apiKey = "xeuAXLNQEeSlPoABiHRH0r7mmPIUnlD9"
    private val httpClient = OkHttpClient()

    fun search(query: String, limit: Int, offset: Int): SearchResponse? {
        if (query.isBlank()) {
            return null
        }
        val url = buildString {
            append(searchUrl)
            append("?api_key=$apiKey")
            append("&limit=$limit")
            append("&offset=$offset")
            append("&q=${URLEncoder.encode(query, "utf-8")}")
        }
        val request = Request.Builder().get().url(url).build()
        val call = httpClient.newCall(request)
        val response = runCatching { call.execute() }
            .onFailure(Throwable::printStackTrace)
            .getOrNull()
            ?: return null

        val body = response.body?.string()
            ?: return null
        return json.decodeFromString(body)
    }
}
