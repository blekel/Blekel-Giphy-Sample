package blekel.giphy.di

import blekel.giphy.data.GiphyService
import org.koin.dsl.module

fun dataModule() = module {
    single { GiphyService() }
}
