package io.github.aouerfelli.subwatcher

object Dependencies {

    const val material = "com.google.android.material:material:${Versions.material}"

    const val coil = "io.coil-kt:coil:${Versions.coil}"

    const val timber = "com.jakewharton.timber:timber-android:${Versions.timber}"

    object KotlinX {
        const val coroutines =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
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
        const val recyclerView =
            "androidx.recyclerview:recyclerview:${Versions.AndroidX.recyclerView}"
        const val swipeRefreshLayout =
            "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.AndroidX.swipeRefreshLayout}"
        const val browser = "androidx.browser:browser:${Versions.AndroidX.browser}"

        object Lifecycle {
            const val liveData =
                "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.AndroidX.lifecycle}"
            const val viewModel =
                "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.AndroidX.lifecycle}"
            const val viewModelSavedState =
                "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.AndroidX.viewModelSavedState}"
        }
    }

    object Dagger {
        const val runtime = "com.google.dagger:dagger:${Versions.dagger}"
        const val compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
        const val androidRuntime = "com.google.dagger:dagger-android-support:${Versions.dagger}"
        const val androidCompiler = "com.google.dagger:dagger-android-processor:${Versions.dagger}"

        object AssistedInject {
            const val runtime =
                "com.squareup.inject:assisted-inject-annotations-dagger2:${Versions.assistedInject}"
            const val compiler =
                "com.squareup.inject:assisted-inject-processor-dagger2:${Versions.assistedInject}"
        }
    }

    object OkHttp {
        const val client = "com.squareup.okhttp3:okhttp:${Versions.okHttp}"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
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
        const val coroutines =
            "com.squareup.sqldelight:coroutines-extensions-jvm:${Versions.sqlDelight}"
    }
}
