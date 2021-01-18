/* TODO: Blocked by https://github.com/gradle/gradle/issues/9270
import com.aouerfelli.subwatcher.GradlePlugins
*/
import com.aouerfelli.subwatcher.Dependencies
import com.aouerfelli.subwatcher.Kotlin

plugins {
  id(com.aouerfelli.subwatcher.GradlePlugins.Android.id)
  kotlin(com.aouerfelli.subwatcher.Kotlin.android)
  kotlin(com.aouerfelli.subwatcher.Kotlin.kapt)
  id(com.aouerfelli.subwatcher.GradlePlugins.SqlDelight.id)
}

android {
  compileSdkVersion(30)
  defaultConfig {
    applicationId = "com.aouerfelli.subwatcher"
    minSdkVersion(21)
    targetSdkVersion(30)
    versionCode = 5
    versionName = "1.0.4"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles("proguard-rules.pro")
    }
  }
  lintOptions {
    isCheckReleaseBuilds = false
    textReport = true
    htmlReport = false
    xmlReport = false
    isExplainIssues = false
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    isCoreLibraryDesugaringEnabled = true
  }
  kotlinOptions {
    jvmTarget = "1.8"
    @Suppress("SuspiciousCollectionReassignment")
    freeCompilerArgs += listOf(
      "-progressive",
      "-XXLanguage:+NewInference",
      "-XXLanguage:+InlineClasses",
      "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
      "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
      "-Xuse-experimental=kotlinx.coroutines.FlowPreview"
    )
  }
  buildFeatures {
    viewBinding = true
  }
}

dependencies {
  implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

  coreLibraryDesugaring(Dependencies.androidDesugarJdkLibs)

  implementation(kotlin(Kotlin.stdlib))
  implementation(platform(Dependencies.KotlinX.Coroutines.bom))
  implementation(Dependencies.KotlinX.Coroutines.android)

  implementation(Dependencies.AndroidX.appcompat)
  implementation(Dependencies.AndroidX.activity)
  implementation(Dependencies.AndroidX.fragment)
  implementation(Dependencies.AndroidX.core)
  implementation(Dependencies.AndroidX.constraintLayout)
  implementation(Dependencies.AndroidX.coordinatorLayout)
  implementation(Dependencies.AndroidX.recyclerView)
  implementation(Dependencies.AndroidX.swipeRefreshLayout)
  implementation(Dependencies.AndroidX.browser)

  implementation(Dependencies.material)
  implementation(Dependencies.insetter)

  implementation(Dependencies.AndroidX.Lifecycle.viewModel)
  implementation(Dependencies.AndroidX.Lifecycle.viewModelSavedState)
  implementation(Dependencies.AndroidX.Lifecycle.process)

  implementation(Dependencies.AndroidX.work)

  implementation(Dependencies.coil)

  implementation(Dependencies.Dagger.runtime)
  kapt(Dependencies.Dagger.compiler)
  implementation(Dependencies.Dagger.androidRuntime)
  kapt(Dependencies.Dagger.androidCompiler)

  implementation(platform(Dependencies.OkHttp.bom))
  implementation(Dependencies.OkHttp.client)
  implementation(Dependencies.OkHttp.loggingInterceptor)
  implementation(Dependencies.Retrofit.client)
  implementation(Dependencies.Retrofit.moshiConverter)
  implementation(Dependencies.Moshi.runtime)
  kapt(Dependencies.Moshi.compiler)

  implementation(Dependencies.SqlDelight.android)
  implementation(Dependencies.SqlDelight.coroutines)

  implementation(Dependencies.timber)

  testImplementation(Dependencies.Test.jUnit)
  testImplementation(Dependencies.KotlinX.Coroutines.test)
  testImplementation(Dependencies.Test.mockK)
  testImplementation(Dependencies.OkHttp.mockWebServer)
  testImplementation(Dependencies.SqlDelight.jvm)
  kaptTest(Dependencies.Dagger.compiler)
}
