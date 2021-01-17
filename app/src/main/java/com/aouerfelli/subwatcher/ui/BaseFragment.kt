package com.aouerfelli.subwatcher.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import dagger.android.support.DaggerFragment
import kotlin.reflect.KClass

abstract class BaseFragment<B : ViewBinding, M : ViewModel>(
  private val viewInflater: (LayoutInflater, ViewGroup?, Boolean) -> B,
  private val viewModelClass: KClass<M>
) : DaggerFragment() {

  protected val supportActivity: AppCompatActivity?
    get() = activity as? AppCompatActivity

  protected var binding: B? = null
    private set

  protected lateinit var viewModel: M
    private set

  protected abstract fun createViewModel(handle: SavedStateHandle): M

  protected abstract fun onBindingCreated(binding: B, savedInstanceState: Bundle?)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val viewModelFactory = object : AbstractSavedStateViewModelFactory(this, arguments) {
      override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
      ): T {
        @Suppress("UNCHECKED_CAST")
        return createViewModel(handle) as T
      }
    }
    viewModel = ViewModelProvider(this, viewModelFactory)[viewModelClass.java]
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return viewInflater(inflater, container, false).also { binding = it }.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    // TODO: Move to activity
    WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
    onBindingCreated(binding!!, savedInstanceState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
}
