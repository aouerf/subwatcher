package io.github.aouerfelli.subwatcher.util

import timber.log.LogcatTree
import timber.log.Timber
import timber.log.Tree

class DebugLogcatTree : Tree() {

  private val logcatTree = LogcatTree()

  private val callingClassTag: String
    get() {
      val stackTrace = Throwable().stackTrace
      // Find the class that called Timber in the stack trace
      val index = stackTrace.indexOfFirst { it.className == Timber::class.java.name } + 1
      val stackElement = stackTrace[index]
      // Get the simple name of the class
      return stackElement.className
        .substringAfterLast('.')
        .substringBefore('$')
    }

  override fun performLog(priority: Int, tag: String?, throwable: Throwable?, message: String?) {
    val inferredTag = tag ?: callingClassTag
    // LogcatTree does the proper tag checks
    logcatTree.log(priority, inferredTag, throwable, message)
  }
}
