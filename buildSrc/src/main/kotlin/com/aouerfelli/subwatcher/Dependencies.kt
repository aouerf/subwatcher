package com.aouerfelli.subwatcher

object Dependencies {

  const val material = "com.google.android.material:material:${Versions.material}"

  const val insetter = "dev.chrisbanes.insetter:insetter:${Versions.insetter}"

  const val coil = "io.coil-kt:coil-base:${Versions.coil}"

  const val timber = "com.jakewharton.timber:timber-android:${Versions.timber}"

  const val androidDesugarJdkLibs =
    "com.android.tools:desugar_jdk_libs:${Versions.androidDesugarJdkLibs}"

  object KotlinX {
    object Coroutines {
      const val bom = "org.jetbrains.kotlinx:kotlinx-coroutines-bom:${Versions.coroutines}"
      const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android"
      const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test"
    }
  }

  object AndroidX {
    const val appcompat = "androidx.appcompat:appcompat:${Versions.AndroidX.appcompat}"
    const val activity = "androidx.activity:activity-ktx:${Versions.AndroidX.activity}"
    const val fragment = "androidx.fragment:fragment-ktx:${Versions.AndroidX.fragment}"
    const val core = "androidx.core:core-ktx:${Versions.AndroidX.core}"
    const val constraintLayout =
      "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintLayout}"
    const val coordinatorLayout =
      "androidx.coordinatorlayout:coordinatorlayout:${Versions.AndroidX.coordinatorLayout}"
    const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.AndroidX.recyclerView}"
    const val swipeRefreshLayout =
      "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.AndroidX.swipeRefreshLayout}"
    const val browser = "androidx.browser:browser:${Versions.AndroidX.browser}"

    object Lifecycle {
      const val viewModel =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.AndroidX.lifecycle}"
      const val viewModelSavedState =
        "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.AndroidX.lifecycle}"
      const val process = "androidx.lifecycle:lifecycle-process:${Versions.AndroidX.lifecycle}"
    }

    const val work = "androidx.work:work-runtime-ktx:${Versions.AndroidX.work}"

    object Hilt {
      const val work = "androidx.hilt:hilt-work:${Versions.AndroidX.hilt}"
      const val compiler = "androidx.hilt:hilt-compiler:${Versions.AndroidX.hilt}"
    }
  }

  object Dagger {
    const val runtime = "com.google.dagger:dagger:${Versions.dagger}"
    const val compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"

    object Hilt {
      const val runtime = "com.google.dagger:hilt-android:${Versions.daggerHilt}"
      const val compiler = "com.google.dagger:hilt-android-compiler:${Versions.daggerHilt}"
    }
  }

  object OkHttp {
    const val bom = "com.squareup.okhttp3:okhttp-bom:${Versions.okHttp}"
    const val client = "com.squareup.okhttp3:okhttp"
    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor"
    const val mockWebServer = "com.squareup.okhttp3:mockwebserver"
  }

  object Retrofit {
    const val client = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val moshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
  }

  object Moshi {
    const val runtime = "com.squareup.moshi:moshi:${Versions.moshi}"
    const val compiler = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
  }

  object SqlDelight {
    const val android = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
    const val jvm = "com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}"
    const val coroutines =
      "com.squareup.sqldelight:coroutines-extensions-jvm:${Versions.sqlDelight}"
  }

  object Test {
    const val jUnit = "org.junit.jupiter:junit-jupiter:${Versions.jUnit}"
    const val mockK = "io.mockk:mockk:${Versions.mockK}"
  }
}
