package io.github.aouerfelli.subwatcher

object GradlePlugins {

    object Android {
        const val classpath = "com.android.tools.build:gradle:4.0.0-alpha07"
        const val id = "com.android.application"
    }

    object Ktlint {
        const val classpath = "org.jlleitschuh.gradle:ktlint-gradle:9.1.1"
        const val id = "org.jlleitschuh.gradle.ktlint"
    }

    object GradleVersions {
        const val classpath = "com.github.ben-manes:gradle-versions-plugin:0.27.0"
        const val id = "com.github.ben-manes.versions"
    }

    object SqlDelight {
        const val classpath = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"
        const val id = "com.squareup.sqldelight"
    }
}
