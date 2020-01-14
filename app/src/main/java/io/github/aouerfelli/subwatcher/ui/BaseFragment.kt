package io.github.aouerfelli.subwatcher.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import dagger.android.support.DaggerFragment
import kotlin.reflect.KClass

abstract class BaseFragment<B : ViewBinding, M : ViewModel> : DaggerFragment() {

  protected val supportActivity: AppCompatActivity?
    get() = activity as? AppCompatActivity

  private var _binding: B? = null
  protected val binding: B
    get() = checkNotNull(_binding) { "This was called before onCreateView()." }

  protected lateinit var viewModel: M
    private set

  protected abstract val viewModelClass: KClass<M>

  protected abstract fun inflateView(
    inflater: LayoutInflater,
    root: ViewGroup?,
    attachToRoot: Boolean
  ): B

  protected abstract fun createViewModel(handle: SavedStateHandle): M

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
    _binding = inflateView(inflater, container, false)
    return binding.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
