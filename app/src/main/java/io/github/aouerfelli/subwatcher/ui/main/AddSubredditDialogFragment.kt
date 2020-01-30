package io.github.aouerfelli.subwatcher.ui.main

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val inputFilters = binding?.subredditField?.editText?.filters.orEmpty()
    val inputWhitespaceFilter = InputFilter { source, _, _, _, _, _ ->
      source.filterNot(Char::isWhitespace)
    }
    // Whitespace filter should come before the maxLength filter so that the character limit doesn't
    // account for possible whitespace
    binding?.subredditField?.editText?.filters = arrayOf(inputWhitespaceFilter, *inputFilters)

    binding?.subredditField?.editText?.requestFocus()
    dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
}
