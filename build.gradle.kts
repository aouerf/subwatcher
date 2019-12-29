/* TODO: Blocked by https://github.com/gradle/gradle/issues/9270
import io.github.aouerfelli.subwatcher.GradlePlugins
import io.github.aouerfelli.subwatcher.Kotlin
*/

buildscript {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
    }

    dependencies {
        classpath(io.github.aouerfelli.subwatcher.GradlePlugins.Android.dependency)
        classpath(kotlin(io.github.aouerfelli.subwatcher.Kotlin.gradlePlugin, io.github.aouerfelli.subwatcher.Kotlin.version))
        classpath(io.github.aouerfelli.subwatcher.GradlePlugins.Ktlint.dependency)
        classpath(io.github.aouerfelli.subwatcher.GradlePlugins.SqlDelight.dependency)
        classpath(io.github.aouerfelli.subwatcher.GradlePlugins.GradleVersions.dependency)
    }
}

apply(plugin = io.github.aouerfelli.subwatcher.GradlePlugins.Ktlint.id)
apply(plugin = io.github.aouerfelli.subwatcher.GradlePlugins.GradleVersions.id)

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
