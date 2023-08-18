package blekel.giphy.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KoinManager(private val application: Application) {

    fun initialize() {
        startKoin {
            androidContext(application)
            modules(getModules())
        }
    }

    private fun getModules() = listOf(
        appModule(application),
        dataModule(),
        viewModelModule(),
    )
}