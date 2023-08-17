package blekel.giphy.data.util

@Suppress("unused")
class RxOptional<T>(
    private var value: T? = null,
) {
    companion object {
        fun <T> empty(): RxOptional<T> = RxOptional()
    }

    fun isEmpty(): Boolean = value == null
    fun isPresent(): Boolean = value != null
    fun getOrNull(): T? = value
    fun get(): T = value ?: throw NoSuchElementException("No value present")
}
