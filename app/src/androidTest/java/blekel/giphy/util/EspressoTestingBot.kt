package blekel.giphy.util

import androidx.test.core.app.ActivityScenario
import blekel.giphy.MainActivity
import org.junit.Before

abstract class EspressoTestingBot {

    @Before
    fun onTestStart() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    fun awaitLoading() {
        Thread.sleep(3_000)
    }
}