package io.github.aouerfelli.subwatcher.network

sealed class Response<out T : Any> {

  data class Success<out T : Any>(val body: T) : Response<T>()

  sealed class Failure : Response<Nothing>() {
    data class Fetch(val code: Int) : Failure() {
      companion object {
        val clientErrorRange = 400..499
        val serverErrorRange = 500..599
      }
    }

    object Parse : Failure()
  }

  object Error : Response<Nothing>()
}
