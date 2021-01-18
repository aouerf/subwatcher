package com.aouerfelli.subwatcher.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface WorkerAssistedInjectFactory<T : ListenableWorker> {
  // This needs to return a type with an assisted-injectable constructor, so it can't return a ListenableWorker directly
  fun create(appContext: Context, workerParams: WorkerParameters): T
}
