package com.aouerfelli.subwatcher.work

import androidx.work.ListenableWorker
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.migration.DisableInstallInCheck
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
@DisableInstallInCheck
// TODO: Switch to hilt-work (https://github.com/google/dagger/issues/2277#issuecomment-761016249)
interface WorkersModule {

  @get:Binds
  @get:IntoMap
  @get:WorkerKey(NewPostsWorker::class)
  val NewPostsWorker.Factory.bindNewPostsWorker: WorkerAssistedInjectFactory<*>
}

@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)
