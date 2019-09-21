package io.github.aouerfelli.subwatcher.network

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.github.aouerfelli.subwatcher.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
object NetworkModule {

    private const val BASE_URL = "https://www.reddit.com"

    @JvmStatic
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            builder.addNetworkInterceptor(loggingInterceptor)
        }
        return builder.build()
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
