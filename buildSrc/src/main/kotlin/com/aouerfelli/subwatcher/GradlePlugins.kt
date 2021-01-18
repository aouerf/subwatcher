package com.aouerfelli.subwatcher

object GradlePlugins {

  object Android {
    const val dependency = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"
    const val id = "com.android.application"
  }

  object Ktlint {
    const val dependency = "org.jlleitschuh.gradle:ktlint-gradle:${Versions.ktlintGradle}"
    const val id = "org.jlleitschuh.gradle.ktlint"
  }

  object GradleVersions {
    const val dependency = "com.github.ben-manes:gradle-versions-plugin:${Versions.gradleVersions}"
    const val id = "com.github.ben-manes.versions"
  }

  object SqlDelight {
    const val dependency = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"
    const val id = "com.squareup.sqldelight"
  }

  object DaggerHilt {
    const val dependency = "com.google.dagger:hilt-android-gradle-plugin:${Versions.dagger}-alpha"
    const val id = "dagger.hilt.android.plugin"
  }
}
