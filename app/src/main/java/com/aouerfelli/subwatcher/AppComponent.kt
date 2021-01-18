package com.aouerfelli.subwatcher

import com.aouerfelli.subwatcher.database.DatabaseModule
import com.aouerfelli.subwatcher.network.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    AndroidInjectionModule::class,
    NetworkModule::class,
    DatabaseModule::class,
    ApplicationModule::class
  ]
)
interface AppComponent : AndroidInjector<SubwatcherApplication> {

  @Component.Factory
  interface Factory {
    fun create(@BindsInstance application: SubwatcherApplication): AppComponent
  }
}
