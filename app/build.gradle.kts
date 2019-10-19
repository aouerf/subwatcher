import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

val versions: Map<String, String> by rootProject.extra

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.sqldelight")
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.2")
    defaultConfig {
        applicationId = "io.github.aouerfelli.subwatcher"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        this as KotlinJvmOptions
        jvmTarget = "1.8"
        @Suppress("SuspiciousCollectionReassignment")
        freeCompilerArgs += listOf(
            "-progressive",
            "-XXLanguage:+NewInference",
            "-XXLanguage:+InlineClasses",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xuse-experimental=kotlinx.coroutines.FlowPreview"
        )
    }
    viewBinding {
        isEnabled = true
    }
    packagingOptions {
        pickFirst("META-INF/kotlinx-coroutines-core.kotlin_module")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${versions["coroutines"]}")

    implementation("androidx.appcompat:appcompat:${versions["appcompat"]}")
    implementation("androidx.activity:activity-ktx:${versions["activity"]}")
    implementation("androidx.fragment:fragment-ktx:${versions["fragment"]}")
    implementation("androidx.core:core-ktx:${versions["core"]}")
    implementation("androidx.constraintlayout:constraintlayout:${versions["constraintlayout"]}")
    implementation("androidx.recyclerview:recyclerview:${versions["recyclerview"]}")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:${versions["swiperefreshlayout"]}")
    implementation("androidx.browser:browser:${versions["browser"]}")

    implementation("com.google.android.material:material:${versions["material"]}")

    implementation("androidx.lifecycle:lifecycle-extensions:${versions["lifecycle"]}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${versions["lifecycle"]}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${versions["lifecycle"]}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${versions["savedstate"]}")

    implementation("com.google.dagger:dagger:${versions["dagger"]}")
    kapt("com.google.dagger:dagger-compiler:${versions["dagger"]}")
    implementation("com.google.dagger:dagger-android-support:${versions["dagger"]}")
    kapt("com.google.dagger:dagger-android-processor:${versions["dagger"]}")
    compileOnly("com.squareup.inject:assisted-inject-annotations-dagger2:${versions["assistedinject"]}")
    kapt("com.squareup.inject:assisted-inject-processor-dagger2:${versions["assistedinject"]}")

    implementation("com.squareup.okhttp3:okhttp:${versions["okhttp"]}")
    implementation("com.squareup.okhttp3:logging-interceptor:${versions["okhttp"]}")
    implementation("com.squareup.retrofit2:retrofit:${versions["retrofit"]}")
    implementation("com.squareup.retrofit2:converter-moshi:${versions["retrofit"]}")
    implementation("com.squareup.moshi:moshi:${versions["moshi"]}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${versions["moshi"]}")

    implementation("com.squareup.sqldelight:android-driver:${versions["sqldelight"]}")
    implementation("com.squareup.sqldelight:coroutines-extensions-jvm:${versions["sqldelight"]}")

    implementation("io.coil-kt:coil:${versions["coil"]}")

    implementation("com.jakewharton.timber:timber-android:${versions["timber"]}")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}
