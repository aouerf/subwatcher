/* TODO: Blocked by https://github.com/gradle/gradle/issues/9270
import io.github.aouerfelli.subwatcher.Deps
import io.github.aouerfelli.subwatcher.Kotlin
*/

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(io.github.aouerfelli.subwatcher.Dependencies.androidGradlePlugin)
        classpath(kotlin(io.github.aouerfelli.subwatcher.Kotlin.gradlePlugin, io.github.aouerfelli.subwatcher.Kotlin.version))
        classpath(io.github.aouerfelli.subwatcher.Dependencies.SqlDelight.gradlePlugin)
    }
}

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
