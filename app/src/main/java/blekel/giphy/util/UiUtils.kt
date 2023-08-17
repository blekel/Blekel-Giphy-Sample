package blekel.giphy.util

import android.content.res.Resources

object UiUtils {

    fun getScreenWidth(): Float {
        val displayMetrics = Resources.getSystem().displayMetrics
        return displayMetrics.widthPixels.toFloat()
    }
}