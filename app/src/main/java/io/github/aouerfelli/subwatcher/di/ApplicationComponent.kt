package io.github.aouerfelli.subwatcher.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.github.aouerfelli.subwatcher.SubwatcherApplication
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ApplicationModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<SubwatcherApplication> {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance application: SubwatcherApplication): ApplicationComponent
    }
}
