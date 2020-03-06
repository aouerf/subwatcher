/* TODO: Blocked by https://github.com/gradle/gradle/issues/9270
import com.aouerfelli.subwatcher.GradlePlugins
import com.aouerfelli.subwatcher.Kotlin
*/

buildscript {
  repositories {
    google()
    jcenter()
    gradlePluginPortal()
  }

  dependencies {
    classpath(com.aouerfelli.subwatcher.GradlePlugins.Android.dependency)
    classpath(
      kotlin(
        com.aouerfelli.subwatcher.Kotlin.gradlePlugin,
        com.aouerfelli.subwatcher.Kotlin.version
      )
    )
    classpath(com.aouerfelli.subwatcher.GradlePlugins.Ktlint.dependency)
    classpath(com.aouerfelli.subwatcher.GradlePlugins.SqlDelight.dependency)
    classpath(com.aouerfelli.subwatcher.GradlePlugins.GradleVersions.dependency)
  }
}

apply(plugin = com.aouerfelli.subwatcher.GradlePlugins.GradleVersions.id)

allprojects {
  apply(plugin = com.aouerfelli.subwatcher.GradlePlugins.Ktlint.id)

  repositories {
    google()
    jcenter()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
  }
}

tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}
