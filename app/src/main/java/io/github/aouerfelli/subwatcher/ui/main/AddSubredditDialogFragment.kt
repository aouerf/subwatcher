package io.github.aouerfelli.subwatcher.ui.main

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.chrisbanes.insetter.doOnApplyWindowInsets
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
    val binding = requireNotNull(binding) { "There is no view currently attached to this dialog." }
    onBindingCreated(binding, savedInstanceState)
  }

  private fun onBindingCreated(
    binding: AddSubredditDialogFragmentBinding,
    savedInstanceState: Bundle?
  ) {
    // FIXME: Long-press (text selection toolbar) seems to trigger insets
    // TODO: Top padding?
    binding.root.doOnApplyWindowInsets { view, insets, initialState ->
      view.updatePadding(
        bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
      )
      view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        leftMargin = insets.systemWindowInsetLeft + initialState.margins.left
        rightMargin = insets.systemWindowInsetRight + initialState.margins.right
      }
    }

    val inputFilters = binding.subredditField.editText?.filters.orEmpty()
    val inputWhitespaceFilter = InputFilter { source, _, _, _, _, _ ->
      source.filterNot(Char::isWhitespace)
    }
    // Whitespace filter should come before the maxLength filter so that the character limit doesn't
    // account for possible whitespace.
    binding.subredditField.editText?.filters = arrayOf(inputWhitespaceFilter, *inputFilters)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    // Disable fitsSystemWindow on the dialog container to allow the dialog to be drawn under the
    // system bars.
    /*dialog?.window?.findViewById<View>(com.google.android.material.R.id.container)
      ?.fitsSystemWindows = false*/
    (view?.parent?.parent?.parent as? View)?.fitsSystemWindows = false

    binding?.subredditField?.editText?.requestFocus()
    dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
}
