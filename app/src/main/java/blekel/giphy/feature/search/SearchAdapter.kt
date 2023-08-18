package blekel.giphy.feature.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import blekel.giphy.databinding.SearchListItemBinding
import blekel.giphy.util.UiUtils
import com.bumptech.glide.RequestManager
import org.koin.core.component.KoinComponent
import timber.log.Timber

class SearchAdapter(
    private val glide: RequestManager,
    gridColumnsCount: Int,
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>(), KoinComponent {

    private var items = listOf<SearchListItem>()
    private val itemDimension = UiUtils.getScreenWidth() / gridColumnsCount

    fun updateItems(values: List<SearchListItem>) {
        val diffCallback = DiffCallback(items, values)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = values
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Timber.v("create view holder")
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = SearchListItemBinding.inflate(inflater)
        itemBinding.imageWidth = itemDimension
        itemBinding.imageHeight = itemDimension
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.v("bind view holder ($position)")
        val item = items.getOrNull(position)
            ?: return
        glide.asGif()
            .load(item.url)
            .override(itemDimension.toInt())
            .into(holder.imageView)
    }


    inner class ViewHolder(itemBinding: SearchListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val imageView = itemBinding.imageView
    }

    private class DiffCallback(
        private val oldItems: List<SearchListItem>,
        private val newItems: List<SearchListItem>,
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldItems.size
        override fun getNewListSize() = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldItems[oldItemPosition] == newItems[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldItems[oldItemPosition] == newItems[newItemPosition]
    }
}
