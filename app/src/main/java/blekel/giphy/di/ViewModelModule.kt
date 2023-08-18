package blekel.giphy.di

import blekel.giphy.feature.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun viewModelModule() = module {
    viewModel { SearchViewModel(giphyService = get()) }
}
