package blekel.giphy.feature.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import blekel.giphy.databinding.FragmentSearchBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter
    private var disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        adapter = SearchAdapter(context, viewModel.gridColumnsCount)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        binding.model = viewModel

        setupRecyclerView()
        binding.queryField.requestFocus()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        disposables.add(
            viewModel.itemsObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onSearchItems)
        )
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(context, viewModel.gridColumnsCount)
        binding.searchResultList.let {
            it.layoutManager = layoutManager
            it.adapter = adapter
            it.itemAnimator = DefaultItemAnimator().apply {
                addDuration = 200
                removeDuration = 200
            }
            it.addOnScrollListener(object : OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val position = layoutManager.findLastVisibleItemPosition()
                    viewModel.checkLoadNext(position)
                }
            })
        }
    }

    private fun onSearchItems(values: List<SearchListItem>) {
        viewModel.updateEmptyMessage(context)
        adapter.updateItems(values)
    }
}