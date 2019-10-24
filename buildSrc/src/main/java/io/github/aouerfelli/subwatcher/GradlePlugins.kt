package io.github.aouerfelli.subwatcher

object GradlePlugins {

    object Android {
        const val classpath = "com.android.tools.build:gradle:4.0.0-alpha01"
        const val id = "com.android.application"
    }

    object Ktlint {
        const val classpath = "org.jlleitschuh.gradle:ktlint-gradle:9.0.0"
        const val id = "org.jlleitschuh.gradle.ktlint"
    }

    object SqlDelight {
        const val classpath = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"
        const val id = "com.squareup.sqldelight"
    }
}
