package io.github.aouerfelli.subwatcher.repository

sealed class Result<out T : Any> {

  @Suppress("NOTHING_TO_INLINE")
  companion object {
    inline fun success() = Success.Empty
    inline fun <T : Any> success(data: T) = Success(data)

    inline fun networkFailure() = Failure.NetworkFailure
    inline fun databaseFailure() = Failure.DatabaseFailure

    inline fun connectionError() = Error.ConnectionError
    inline fun networkError() = Error.NetworkError
  }

  data class Success<out T : Any>(val data: T) : Result<T>() {
    object Empty : Result<Nothing>()
  }

  sealed class Failure : Result<Nothing>() {
    object NetworkFailure : Failure()
    object DatabaseFailure : Failure()
  }

  sealed class Error : Result<Nothing>() {
    object ConnectionError : Error()
    object NetworkError : Error()
  }

  override fun toString(): String {
    return when (this) {
      Success.Empty -> "Success"
      is Success -> "Success($data)"
      is Failure -> "Failure[${this.javaClass.simpleName}]"
      is Error -> "Error[${this.javaClass.simpleName}]"
    }
  }
}
