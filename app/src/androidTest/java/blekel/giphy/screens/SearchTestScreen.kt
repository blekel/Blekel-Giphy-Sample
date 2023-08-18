package blekel.giphy.screens

import android.view.View
import blekel.giphy.R
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher

class SearchTestScreen : Screen<SearchTestScreen>() {
    class ListItem(parent: Matcher<View>) : KRecyclerItem<ListItem>(parent) {
        val image = KImageView { withId(R.id.imageView) }
    }

    val emptyMessage = KTextView { withId(R.id.emptyMessage) }
    val itemsList = KRecyclerView(
        { withId(R.id.searchResultList) },
        { itemType(::ListItem) }
    )
    val queryField = KEditText { withId(R.id.queryField) }
}