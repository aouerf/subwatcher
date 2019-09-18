package io.github.aouerfelli.subwatcher

import dagger.android.support.DaggerApplication
import io.github.aouerfelli.subwatcher.di.DaggerApplicationComponent

class SubwatcherApplication : DaggerApplication() {

    override fun applicationInjector() = DaggerApplicationComponent.factory().create(this)
}
