package blekel.giphy.util.binding

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import kotlin.math.roundToInt

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("width")
    fun setWidth(view: View, value: Float) {
        view.layoutParams = view.layoutParams.apply {
            width = value.roundToInt()
        }
    }

    @JvmStatic
    @BindingAdapter("height")
    fun setHeight(view: View, value: Float) {
        view.layoutParams = view.layoutParams.apply {
            height = value.roundToInt()
        }
    }

    @JvmStatic
    @BindingAdapter("android:visibility")
    fun setVisibility(view: View, value: Boolean) {
        view.isVisible = value
    }
}