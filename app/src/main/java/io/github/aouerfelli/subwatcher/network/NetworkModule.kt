package io.github.aouerfelli.subwatcher.network

import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import timber.log.Timber
import timber.log.debug

@Module
object NetworkModule {

    const val BASE_URL = "https://www.reddit.com"
    private const val LOG_TAG = "OkHttp"

    @Provides
    @Singleton
    fun provideRedditService(): RedditService {
        val logTree = Timber.tagged(LOG_TAG)
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                logTree.debug { message }
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create()
    }
}
