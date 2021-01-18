package com.aouerfelli.subwatcher.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class ViewBindingFragment<B : ViewBinding>(
  private val viewInflater: (LayoutInflater, ViewGroup?, Boolean) -> B
) : Fragment() {

  protected val supportActivity: AppCompatActivity?
    get() = activity as? AppCompatActivity

  protected var binding: B? = null
    private set

  protected abstract fun onBindingCreated(binding: B, savedInstanceState: Bundle?)

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return viewInflater(inflater, container, false).also { binding = it }.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    onBindingCreated(binding!!, savedInstanceState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
}
