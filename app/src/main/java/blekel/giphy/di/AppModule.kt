package blekel.giphy.di

import android.app.Application
import com.bumptech.glide.Glide
import org.koin.dsl.module

fun appModule(application: Application) = module {
    single { Glide.with(application) }
}
