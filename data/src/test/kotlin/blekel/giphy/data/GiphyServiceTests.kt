package blekel.giphy.data

import blekel.giphy.data.util.json
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GiphyServiceTests {

    private val service = GiphyService()

    @Test
    fun testSearch() {
        val response = service.search("sunny")
        println(json.encodeToString(response))

        assertNotNull(response)
        response!!

        assertTrue(response.data.isNotEmpty())
        assertTrue(response.pagination.count > 0)
        assertEquals(response.data.size, response.pagination.count)
    }
}