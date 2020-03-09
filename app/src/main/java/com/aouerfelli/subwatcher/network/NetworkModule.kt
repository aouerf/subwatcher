package com.aouerfelli.subwatcher.network

import dagger.Lazy
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import timber.log.Timber
import timber.log.debug
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

data class NetworkDetails(
  val baseUrl: HttpUrl,
  val cacheDir: File? = null
)

@Module
object NetworkModule {

  @Retention(AnnotationRetention.BINARY)
  @Qualifier
  private annotation class InternalApi

  @Provides
  @NetworkModule.InternalApi
  fun provideCache(networkDetails: NetworkDetails): Cache? {
    val cacheSize = 10L * 1024 * 1024 // 10 MiB
    return networkDetails.cacheDir?.let { Cache(it, cacheSize) }
  }

  @Provides
  @NetworkModule.InternalApi
  @Singleton
  fun provideOkHttpClient(@NetworkModule.InternalApi cache: Cache?): OkHttpClient {
    val logTree = Timber.tagged("OkHttp")
    val logger = object : HttpLoggingInterceptor.Logger {
      override fun log(message: String) {
        logTree.debug { message }
      }
    }
    val loggingInterceptor = HttpLoggingInterceptor(logger).apply {
      level = HttpLoggingInterceptor.Level.BASIC
    }
    return OkHttpClient.Builder()
      .cache(cache)
      .addNetworkInterceptor(loggingInterceptor)
      .build()
  }

  @Provides
  @Singleton
  fun provideRedditService(
    @NetworkModule.InternalApi okHttpClient: Lazy<OkHttpClient>,
    networkDetails: NetworkDetails
  ): RedditService {
    val callFactory = object : Call.Factory {
      override fun newCall(request: Request) = okHttpClient.get().newCall(request)
    }
    val retrofit = Retrofit.Builder()
      .callFactory(callFactory)
      .baseUrl(networkDetails.baseUrl)
      .addConverterFactory(MoshiConverterFactory.create())
      .build()

    return retrofit.create()
  }
}
