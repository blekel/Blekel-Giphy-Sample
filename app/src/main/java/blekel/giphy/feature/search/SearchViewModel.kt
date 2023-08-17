package blekel.giphy.feature.search

import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import blekel.giphy.data.GiphyService
import blekel.giphy.data.SearchResponse
import blekel.giphy.data.util.RxOptional
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SearchViewModel : ViewModel() {

    val query = ObservableField("")
    val queryObservable = PublishSubject.create<String>()

    private var items = listOf<String>()
    val itemsObservable = BehaviorSubject.create<List<String>>()

    private val giphyService = GiphyService()
    private val propertyChangedCallback = makePropertyChangedCallback()
    private val searchDebounceTimeoutMs = 300L
    private var searchDisposable: Disposable? = null

    fun setup() {
        query.addOnPropertyChangedCallback(propertyChangedCallback)
        searchDisposable = queryObservable
            .subscribeOn(Schedulers.computation())
            .debounce(searchDebounceTimeoutMs, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.io())
            .map(::onSearchRequest)
            .doOnError { Timber.d("error: $it") }
            .toFlowable(BackpressureStrategy.LATEST)
            .subscribe(::onSearchResponse)
    }

    private fun onSearchRequest(query: String): RxOptional<SearchResponse> {
        Timber.d("search: [$query]")
        val response = giphyService.search(query)
        return RxOptional(response)
    }

    private fun onSearchResponse(response: RxOptional<SearchResponse>) {
        val values = getSearchResponseItems(response)
        Timber.d("items count: ${values.size}")
        items = values
        itemsObservable.onNext(items)
    }

    private fun getSearchResponseItems(response: RxOptional<SearchResponse>): List<String> {
        if (response.isEmpty()) {
            return emptyList()
        }
        return response.get()
            .data
            .map { it.images.preview.url }
    }

    private fun makePropertyChangedCallback(): OnPropertyChangedCallback {
        return object : OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender == query) {
                    queryObservable.onNext(query.get()!!)
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