package com.aouerfelli.subwatcher.ui.main

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import com.aouerfelli.subwatcher.R
import com.aouerfelli.subwatcher.databinding.AddSubredditDialogFragmentBinding
import com.aouerfelli.subwatcher.repository.SubredditName
import com.aouerfelli.subwatcher.repository.isValid
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.chrisbanes.insetter.applySystemWindowInsetsToMargin
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding

class AddSubredditDialogFragment : BottomSheetDialogFragment() {

  companion object {
    const val SUBREDDIT_NAME_KEY = "subreddit_name"
  }

  private var binding: AddSubredditDialogFragmentBinding? = null

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    dialog.behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
      override fun onStateChanged(bottomSheet: View, newState: Int) {
        // Prevent expanded bottom sheet to avoid removing corners set with shapeAppearanceOverlay
        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
          dialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
      }

      override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    })

    return dialog
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return AddSubredditDialogFragmentBinding.inflate(inflater, container, false)
      .also { binding = it }
      .root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    // This is only called after onCreateView(), which is where binding gets assigned.
    val binding = binding!!

    binding.root.applySystemWindowInsetsToPadding(
      bottom = true
    )
    binding.root.applySystemWindowInsetsToMargin(
      left = true,
      right = true
    )

    binding.addButton.setOnClickListener {
      val subredditName = binding.subredditField.editText?.text?.toString()
      if (!validateSubredditName(subredditName)) {
        binding.subredditField.error = getString(R.string.add_subreddit_dialog_error)
        return@setOnClickListener
      }
      setFragmentResult(
        MainFragment.ADD_SUBREDDIT_REQUEST_KEY,
        bundleOf(SUBREDDIT_NAME_KEY to subredditName)
      )
      dismiss()
    }

    binding.subredditField.editText?.setOnEditorActionListener { _, actionId, event: KeyEvent? ->
      val isDone = actionId == EditorInfo.IME_ACTION_DONE
      val isEnter = event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
      if (isDone || isEnter) {
        binding.addButton.callOnClick()
        true
      } else {
        false
      }
    }

    val inputFilters = binding.subredditField.editText?.filters.orEmpty()
    val inputWhitespaceFilter = InputFilter { source, _, _, _, _, _ ->
      source.filterNot(Char::isWhitespace)
    }
    // Whitespace filter should come before the maxLength filter so that the character limit doesn't
    // account for possible whitespace.
    binding.subredditField.editText?.filters = arrayOf(inputWhitespaceFilter, *inputFilters)

    binding.subredditField.editText?.doAfterTextChanged { text ->
      val isValid = validateSubredditName(text?.toString())
      binding.addButton.isEnabled = isValid
      if (isValid) {
        binding.subredditField.isErrorEnabled = false
      }
    }
  }

  private fun validateSubredditName(name: String?): Boolean {
    return !name.isNullOrEmpty() && SubredditName(name.toString()).isValid
  }

  override fun onStart() {
    super.onStart()
    // Disable fitsSystemWindow on the dialog container to allow the dialog to be drawn under the
    // system bars.
    // The inner container also has fitsSystemWindow disabled to prevent the text selection toolbar
    // from adding padding to the view.
    // Do this only on API versions where light navigation bars are supported.
    if (Build.VERSION.SDK_INT >= 27) {
      (view?.parent?.parent as? View)?.apply {
        fitsSystemWindows = false
        (parent as? View)?.fitsSystemWindows = false
      }
    }
  }

  override fun onResume() {
    super.onResume()
    // Bring up soft input method automatically.
    binding?.subredditField?.editText?.requestFocus()
    dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
}
