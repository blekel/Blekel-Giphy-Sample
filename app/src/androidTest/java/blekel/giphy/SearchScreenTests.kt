package blekel.giphy

import blekel.giphy.screens.SearchTestScreen
import blekel.giphy.util.EspressoTestingBot
import io.github.kakaocup.kakao.screen.Screen.Companion.onScreen
import org.junit.Test

class SearchScreenTests : EspressoTestingBot() {

    @Test
    fun testNoQuery() {
        onScreen<SearchTestScreen> {
            emptyMessage.hasText(R.string.search_empty_no_query)
            emptyMessage.isDisplayed()
            itemsList.hasSize(0)
            queryField.hasText("")
        }
    }

    @Test
    fun testNoSearchResults() {
        onScreen<SearchTestScreen> {
            queryField.typeText("LoremIpsum123@$")
            awaitLoading()

            emptyMessage.hasText(R.string.search_empty_no_data)
            emptyMessage.isDisplayed()
            itemsList.hasSize(0)
        }
    }

    @Test
    fun testSearchSuccess() {
        onScreen<SearchTestScreen> {
            queryField.typeText("sunny")
            awaitLoading()

            emptyMessage.isNotDisplayed()
            itemsList.firstChild<SearchTestScreen.ListItem> {
                isDisplayed()
            }
        }
    }
}