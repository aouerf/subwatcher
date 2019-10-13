package io.github.aouerfelli.subwatcher.network

sealed class NetworkResponse<out T : Any> {
    data class Success<T : Any>(val body: T): NetworkResponse<T>()
    sealed class Failure : NetworkResponse<Nothing>() {
        data class Fetch(val code: Int) : Failure()
        object Parse : Failure()
    }
    object Error : NetworkResponse<Nothing>()
}
