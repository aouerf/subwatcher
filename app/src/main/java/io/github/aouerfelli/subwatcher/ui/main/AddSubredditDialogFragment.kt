package io.github.aouerfelli.subwatcher.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.aouerfelli.subwatcher.databinding.AddSubredditDialogFragmentBinding

class AddSubredditDialogFragment : BottomSheetDialogFragment() {

  private var binding: AddSubredditDialogFragmentBinding? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return AddSubredditDialogFragmentBinding.inflate(inflater, container, false)
      .also { binding = it }
      .root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
}
