package blekel.giphy.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val data: List<SearchItem>,
    val pagination: SearchPagination,
)

@Serializable
data class SearchPagination(
    @SerialName("total_count")
    val totalCount: Int,
    val count: Int,
    val offset: Int,
)

@Serializable
data class SearchItem(
    val id: String,
    val type: String,
    val title: String,
    val url: String,
    val images: SearchItemImages,
)

@Serializable
data class SearchItemImages(
    @SerialName("preview_gif")
    val preview: SearchItemImage,
)

@Serializable
data class SearchItemImage(
    val width: Int,
    val height: Int,
    val url: String,
)

