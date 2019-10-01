package io.github.aouerfelli.subwatcher.ui.main

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.aouerfelli.subwatcher.di.ViewModelFactoryModule
import io.github.aouerfelli.subwatcher.di.ViewModelKey

@Module
abstract class MainModule {

    @ContributesAndroidInjector(modules = [ViewModelFactoryModule::class])
    abstract fun contributeMainFragment(): MainFragment

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindViewModel(viewModel: MainViewModel): ViewModel
}
