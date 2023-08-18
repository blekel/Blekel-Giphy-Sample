package blekel.giphy.feature.search

import android.content.Context
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blekel.giphy.R
import blekel.giphy.data.GiphyService
import blekel.giphy.data.SearchPagination
import blekel.giphy.data.SearchResponse
import blekel.giphy.data.util.RxOptional
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import io.reactivex.rxjava3.core.Observable as RxObservable

class SearchViewModel(
    private val giphyService: GiphyService
) : ViewModel() {

    val emptyMessage = ObservableField("")
    val isEmptyMessageVisible = ObservableBoolean()

    val query = ObservableField("")
    val queryObservable = PublishSubject.create<String>()

    private var items = listOf<SearchListItem>()
    val itemsObservable = BehaviorSubject.create<List<SearchListItem>>()

    private val offsetObservable = BehaviorSubject.create<Int>()

    val gridColumnsCount = 3
    private val pageSize = 15 * gridColumnsCount
    private val loadNextThreshold = 10 * gridColumnsCount

    private val propertyChangedCallback = makePropertyChangedCallback()

    private val searchDebounceTimeoutMs = 300L
    private var searchDisposable: Disposable? = null
    private var pagination: SearchPagination? = null
    private var wasQuery = ""

    init {
        itemsObservable.onNext(items)
        offsetObservable.onNext(0)
        query.addOnPropertyChangedCallback(propertyChangedCallback)
        searchDisposable = observeSearch()
    }

    fun getQueryText() = query.get()!!.trim()

    fun updateEmptyMessage(context: Context?) {
        if (context == null) {
            return
        }
        val hasNoQuery = getQueryText().isBlank()
        val hasNoData = items.isEmpty()
        val emptyText = when {
            hasNoQuery -> context.getString(R.string.search_empty_no_query)
            hasNoData -> context.getString(R.string.search_empty_no_data)
            else -> ""
        }
        viewModelScope.launch {
            delay(200L)
            emptyMessage.set(emptyText)
            isEmptyMessageVisible.set(hasNoData)
        }
    }

    fun checkLoadNext(lastVisiblePosition: Int) {
        val itemsCount = items.size
        val totalCount = pagination?.totalCount ?: 0
        val hasToLoadNext = itemsCount < totalCount
                && lastVisiblePosition >= (itemsCount - loadNextThreshold)
        if (hasToLoadNext) {
            offsetObservable.onNext(itemsCount - 1)
        }
    }

    private fun observeSearch(): Disposable {
        return RxObservable.combineLatest(
            queryObservable
                .debounce(searchDebounceTimeoutMs, TimeUnit.MILLISECONDS),
            offsetObservable,
            ::SearchRequest
        ).subscribeOn(Schedulers.computation())
            .distinctUntilChanged()
            .observeOn(Schedulers.io())
            .map(::onSearchRequest)
            .doOnError { Timber.e("error: $it") }
            .toFlowable(BackpressureStrategy.LATEST)
            .subscribe(::onSearchResponse)
    }

    private fun onSearchRequest(request: SearchRequest): RxOptional<SearchResponse> {
        val query = request.query
        var offset = request.offset
        if (this.wasQuery != query) {
            this.wasQuery = query
            pagination = null
            items = emptyList()
            offset = 0
        }
        Timber.d("search query: '$query', offset: $offset")
        val response = giphyService.search(
            query = query,
            limit = pageSize,
            offset = offset,
        )
        return RxOptional(response)
    }

    private fun onSearchResponse(response: RxOptional<SearchResponse>) {
        val values = makeSearchListItems(response)
        if (response.isPresent()) {
            pagination = response.get().pagination
        }
        items = items.toMutableList().apply { addAll(values) }
        itemsObservable.onNext(items)

        val valuesCount = values.size
        val loadedCount = items.size
        val totalCount = pagination?.totalCount
        Timber.d(
            "response items count: $valuesCount, " +
                    "loaded: $loadedCount, total: $totalCount"
        )
    }

    private fun makeSearchListItems(response: RxOptional<SearchResponse>): List<SearchListItem> {
        if (response.isEmpty()) {
            return emptyList()
        }
        val startIndex = items.size
        return response.get()
            .data
            .map { it.images.preview.url }
            .mapIndexed { i, it -> SearchListItem(startIndex + i, it) }
    }

    private fun makePropertyChangedCallback(): OnPropertyChangedCallback {
        return object : OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender == query) {
                    queryObservable.onNext(getQueryText())
                }
            }
        }
    }

    override fun onCleared() {
        query.removeOnPropertyChangedCallback(propertyChangedCallback)
        searchDisposable?.dispose()
        super.onCleared()
    }
}