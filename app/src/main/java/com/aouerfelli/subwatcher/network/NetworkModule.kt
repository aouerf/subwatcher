package com.aouerfelli.subwatcher.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import timber.log.Timber
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

data class NetworkDetails(
  val baseUrl: HttpUrl,
  val cacheDir: File? = null
)

@Module
@DisableInstallInCheck
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
    val logger = HttpLoggingInterceptor.Logger { message -> Timber.tag("OkHttp").d(message) }
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
  fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
  }

  @OptIn(ExperimentalSerializationApi::class)
  @Provides
  @Singleton
  fun provideRedditService(
    @NetworkModule.InternalApi okHttpClient: Lazy<OkHttpClient>,
    json: Json,
    networkDetails: NetworkDetails
  ): RedditService {
    val callFactory = Call.Factory { request -> okHttpClient.get().newCall(request) }
    val jsonConverterFactory = json.asConverterFactory("application/json".toMediaType())
    val retrofit = Retrofit.Builder()
      .callFactory(callFactory)
      .baseUrl(networkDetails.baseUrl)
      .addConverterFactory(jsonConverterFactory)
      .build()

    return retrofit.create()
  }
}
