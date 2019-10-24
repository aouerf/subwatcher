package io.github.aouerfelli.subwatcher

object Dependencies {

    const val material = "com.google.android.material:material:1.2.0-alpha01"

    const val coil = "io.coil-kt:coil:0.8.0"

    const val timber = "com.jakewharton.timber:timber-android:5.0.0-SNAPSHOT"

    object KotlinX {
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.1.0"
        const val activity = "androidx.activity:activity-ktx:1.1.0-beta01"
        const val fragment = "androidx.fragment:fragment-ktx:1.2.0-beta02"
        const val core = "androidx.core:core-ktx:1.2.0-beta01"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta3"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0-beta05"
        const val swipeRefreshLayout =
            "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0-alpha03"
        const val browser = "androidx.browser:browser:1.2.0-alpha08"

        object Lifecycle {
            private const val version = "2.2.0-beta01"

            const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val viewModelSavedState =
                "androidx.lifecycle:lifecycle-viewmodel-savedstate:1.0.0-beta01"
        }
    }

    object Dagger {
        private const val version = "2.25.2"

        const val runtime = "com.google.dagger:dagger:$version"
        const val compiler = "com.google.dagger:dagger-compiler:$version"
        const val androidRuntime = "com.google.dagger:dagger-android-support:$version"
        const val androidCompiler = "com.google.dagger:dagger-android-processor:$version"

        object AssistedInject {
            private const val version = "0.5.0"

            const val runtime = "com.squareup.inject:assisted-inject-annotations-dagger2:$version"
            const val compiler = "com.squareup.inject:assisted-inject-processor-dagger2:$version"
        }
    }

    object OkHttp {
        private const val version = "4.2.2"

        const val client = "com.squareup.okhttp3:okhttp:$version"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Retrofit {
        private const val version = "2.6.2"

        const val client = "com.squareup.retrofit2:retrofit:$version"
        const val moshiConverter = "com.squareup.retrofit2:converter-moshi:$version"
    }

    object Moshi {
        private const val version = "1.8.0"

        const val runtime = "com.squareup.moshi:moshi:$version"
        const val compiler = "com.squareup.moshi:moshi-kotlin-codegen:$version"
    }

    object SqlDelight {
        private const val version = Versions.sqlDelight

        const val android = "com.squareup.sqldelight:android-driver:$version"
        const val coroutines = "com.squareup.sqldelight:coroutines-extensions-jvm:$version"
    }
}
