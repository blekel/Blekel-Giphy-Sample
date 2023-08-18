package blekel.giphy

import android.app.Application
import blekel.giphy.di.KoinManager
import timber.log.Timber

class GiphyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        KoinManager(this).initialize()
    }
}