package blekel.giphy.feature.search

data class SearchRequest(
    val query: String,
    val offset: Int,
)

data class SearchListItem(
    val index: Int,
    val url: String,
)
