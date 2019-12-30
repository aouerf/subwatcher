package io.github.aouerfelli.subwatcher

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import io.github.aouerfelli.subwatcher.database.DatabaseModule
import io.github.aouerfelli.subwatcher.network.NetworkModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AssistedInjectModule::class,
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

@AssistedModule
@Module(includes = [AssistedInject_AssistedInjectModule::class])
interface AssistedInjectModule
