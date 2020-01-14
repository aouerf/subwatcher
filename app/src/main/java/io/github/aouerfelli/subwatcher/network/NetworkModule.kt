package io.github.aouerfelli.subwatcher.network

import android.content.Context
import dagger.Lazy
import dagger.Module
import dagger.Provides
import io.github.aouerfelli.subwatcher.repository.redditBaseUrl
import javax.inject.Qualifier
import javax.inject.Singleton
import okhttp3.Cache
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import timber.log.Timber
import timber.log.debug

@Module
object NetworkModule {

  private const val BASE_URL = redditBaseUrl
  private const val LOG_TAG = "OkHttp"

  @Retention(AnnotationRetention.BINARY)
  @Qualifier
  private annotation class InternalApi

  @Provides
  @InternalApi
  fun provideCache(context: Context): Cache {
    val cacheSize = 10L * 1024 * 1024 // 10 MiB
    return Cache(context.cacheDir, cacheSize)
  }

  @Provides
  @InternalApi
  @Singleton
  fun provideOkHttpClient(@InternalApi cache: Cache): OkHttpClient {
    val logTree = Timber.tagged(LOG_TAG)
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
  fun provideRedditService(@InternalApi okHttpClient: Lazy<OkHttpClient>): RedditService {
    val callFactory = object : Call.Factory {
      override fun newCall(request: Request) = okHttpClient.get().newCall(request)
    }
    val retrofit = Retrofit.Builder()
      .callFactory(callFactory)
      .baseUrl(BASE_URL)
      .addConverterFactory(MoshiConverterFactory.create())
      .build()

    return retrofit.create()
  }
}
