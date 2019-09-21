package io.github.aouerfelli.subwatcher.network

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import timber.log.Timber
import timber.log.debug
import javax.inject.Singleton

@Module
object NetworkModule {

    private const val BASE_URL = "https://www.reddit.com"
    private const val LOG_TAG = "OkHttp"

    @JvmStatic
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logTree = Timber.tagged(LOG_TAG)
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                logTree.debug { message }
            }
        }).apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .build()
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideRedditService(retrofit: Retrofit): RedditService {
        return retrofit.create()
    }
}
