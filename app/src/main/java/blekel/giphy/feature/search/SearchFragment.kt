package blekel.giphy.feature.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import blekel.giphy.databinding.FragmentSearchBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: SearchAdapter
    private val gridColumnsCount = 3
    private var disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        viewModel.setup()
        adapter = SearchAdapter(context, gridColumnsCount)
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
        binding.searchResultList.let {
            it.layoutManager = GridLayoutManager(context, gridColumnsCount)
            it.adapter = adapter
        }
    }

    private fun onSearchItems(values: List<String>) {
        binding.emptyView.isVisible = values.isEmpty()
        adapter.updateItems(values)
    }
}