package blekel.giphy.feature.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import blekel.giphy.databinding.SearchListItemBinding
import blekel.giphy.util.UiUtils
import com.bumptech.glide.Glide
import timber.log.Timber

class SearchAdapter(context: Context, gridColumnsCount: Int) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(itemBinding: SearchListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val imageView = itemBinding.imageView
    }

    private var items = listOf<String>()
    private val glide = Glide.with(context)
    private val itemDimension = UiUtils.getScreenWidth() / gridColumnsCount

    fun updateItems(values: List<String>) {
        items = values
        // TODO: impl
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Timber.d("create view holder")
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = SearchListItemBinding.inflate(inflater)
        itemBinding.imageWidth = itemDimension
        itemBinding.imageHeight = itemDimension
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.d("bind view holder ($position)")
        val item = items[position]
        glide.asGif()
            .load(item)
            .override(itemDimension.toInt())
            .into(holder.imageView)
    }
}