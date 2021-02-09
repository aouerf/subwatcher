# subwatcher

[![Android CI](https://github.com/aouerfelli/subwatcher/workflows/Android%20CI/badge.svg)](https://github.com/aouerfelli/subwatcher/actions)

Subwatcher is an Android application that allows you to follow subreddits to be notified hourly of any new posts in them.

This is not intended to be a replacement for a full-blown Reddit client, but rather work alongside one to offer a complete experience.

<a href="https://play.google.com/store/apps/details?id=com.aouerfelli.subwatcher"><img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" width="200"/></a>

## Libraries

* [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) for managing code asynchronously
* [Dagger](https://github.com/google/dagger) ([Hilt](https://github.com/google/dagger/tree/master/java/dagger/hilt)) for dependency injection
* [Retrofit](https://github.com/square/retrofit) for handling HTTP requests
* [Moshi](https://github.com/square/moshi) for serializing and deserializing JSON
* [SQLDelight](https://github.com/cashapp/sqldelight) for (SQLite) database management
* [WorkManager](https://developer.android.com/jetpack/androidx/releases/work) for running background jobs
* [Coil](https://github.com/coil-kt/coil) for image loading

## Architecture

The app is separated into two modules:
* [app](app) is an Android app module that contains the UI layer and anything platform-specific (such as notifications and background task management).
* [data](data) is a JVM library module that contains both the domain and data (network and database) layers.
