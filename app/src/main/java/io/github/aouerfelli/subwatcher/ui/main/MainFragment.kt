package io.github.aouerfelli.subwatcher.ui.main

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isGone
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.aouerfelli.subwatcher.BuildConfig
import io.github.aouerfelli.subwatcher.R
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.databinding.MainFragmentBinding
import io.github.aouerfelli.subwatcher.repository.Result
import io.github.aouerfelli.subwatcher.repository.SubredditName
import io.github.aouerfelli.subwatcher.repository.asUrl
import io.github.aouerfelli.subwatcher.ui.BaseFragment
import io.github.aouerfelli.subwatcher.util.EventSnackbar
import io.github.aouerfelli.subwatcher.util.SnackbarLength
import io.github.aouerfelli.subwatcher.util.extensions.buildCustomTabsIntent
import io.github.aouerfelli.subwatcher.util.extensions.launch
import io.github.aouerfelli.subwatcher.util.extensions.onSwipe
import io.github.aouerfelli.subwatcher.util.extensions.setThemeColorScheme
import io.github.aouerfelli.subwatcher.util.makeSnackbar
import io.github.aouerfelli.subwatcher.util.observeOn
import io.github.aouerfelli.subwatcher.util.toAndroidString
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import timber.log.warn

class MainFragment : BaseFragment<MainFragmentBinding, MainViewModel>() {

  companion object {
    private const val ADD_SUBREDDIT_REQUEST_CODE = 2

    private const val NOTIFICATION_NEW_POSTS_CHANNEL_ID = "newSubredditPosts"
    private const val NOTIFICATION_NEW_POSTS_ID = 1
  }

  @Inject
  lateinit var viewModelFactory: MainViewModel.Factory
  override val viewModelClass = MainViewModel::class

  @Inject
  lateinit var imageLoader: ImageLoader
  private lateinit var subredditListAdapter: SubredditListAdapter

  private val eventSnackbar = EventSnackbar()

  override fun inflateView(
    inflater: LayoutInflater,
    root: ViewGroup?,
    attachToRoot: Boolean
  ): MainFragmentBinding {
    return MainFragmentBinding.inflate(inflater, root, attachToRoot)
  }

  override fun createViewModel(handle: SavedStateHandle) = viewModelFactory.create(handle)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // TODO: Remove notification handling away from fragment
    val applicationContext = requireContext().applicationContext
    val notificationManager = NotificationManagerCompat.from(applicationContext)
    if (Build.VERSION.SDK_INT >= 26) {
      // TODO: String resources
      val channelName = "New subreddit posts"
      val channelDescription = "Shows the number of new posts for a subreddit since the last check."
      val channelImportance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(
        NOTIFICATION_NEW_POSTS_CHANNEL_ID,
        channelName,
        channelImportance
      ).apply {
        description = channelDescription
      }
      notificationManager.createNotificationChannel(channel)
    }
    // TODO: Check for all subreddits in database
    val subredditName = SubredditName("androiddev")
    val customTabsIntent = subredditName.asUrl().buildCustomTabsIntent()
    val contentIntent = PendingIntent.getActivity(
      applicationContext, 0, customTabsIntent.intent, 0, customTabsIntent.startAnimationBundle
    )
    val notification =
      NotificationCompat.Builder(applicationContext, NOTIFICATION_NEW_POSTS_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_reddit_mark)
        .setContentTitle("r/${subredditName.name}")
        .setContentText("24 new posts") // TODO: String resource
        .setContentIntent(contentIntent)
        .setAutoCancel(true)
        .build()
    notificationManager.notify(subredditName.name, NOTIFICATION_NEW_POSTS_ID, notification)
  }

  override fun onBindingCreated(binding: MainFragmentBinding, savedInstanceState: Bundle?) {
    subredditListAdapter = SubredditListAdapter(imageLoader)
    binding.subredditList.adapter = subredditListAdapter
    binding.subredditList.onSwipe { viewHolder, _ ->
      val position = viewHolder.adapterPosition
      val item = subredditListAdapter.currentList[position]
      viewModel.delete(item)
    }
    binding.subredditList.doOnApplyWindowInsets { view, insets, initialState ->
      view.updatePadding(
        left = insets.systemWindowInsetLeft + initialState.paddings.left,
        top = insets.systemWindowInsetTop + initialState.paddings.top,
        right = insets.systemWindowInsetRight + initialState.paddings.right,
        bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
      )
    }

    binding.subredditsRefresh.setThemeColorScheme()
    binding.subredditsRefresh.setOnRefreshListener {
      viewModel.refresh()
    }

    binding.addSubredditButton.setOnClickListener {
      val dialogFragment = AddSubredditDialogFragment()
      dialogFragment.setTargetFragment(this, ADD_SUBREDDIT_REQUEST_CODE)
      dialogFragment.show(requireActivity().supportFragmentManager, dialogFragment.tag)
    }
    binding.addSubredditButton.setOnLongClickListener {
      if (BuildConfig.DEBUG) {
        viewModel.add("random")
        true
      } else {
        false
      }
    }
    binding.addSubredditButton.doOnApplyWindowInsets { view, insets, initialState ->
      view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        updateMargins(
          left = insets.systemWindowInsetLeft + initialState.margins.left,
          top = insets.systemWindowInsetTop + initialState.margins.top,
          right = insets.systemWindowInsetRight + initialState.margins.right,
          bottom = insets.systemWindowInsetBottom + initialState.margins.bottom
        )
      }
    }

    viewModel.subredditList
      .onEach { list ->
        subredditListAdapter.submitList(list)
        val isListNotEmpty = list.isNotEmpty()
        binding.subredditsRefresh.isEnabled = isListNotEmpty
        binding.emptyStateContainer.isGone = isListNotEmpty
      }
      .launchIn(viewLifecycleOwner.lifecycleScope)
    viewModel.isLoading
      .onEach { binding.subredditsRefresh.isRefreshing = it }
      .launchIn(viewLifecycleOwner.lifecycleScope)
    viewModel.refreshedSubreddits.observeOn(viewLifecycleOwner, ::onSubredditsRefreshed)
    viewModel.addedSubreddit.observeOn(viewLifecycleOwner, ::onSubredditAdded)
    viewModel.deletedSubreddit.observeOn(viewLifecycleOwner, ::onSubredditDeleted)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode != Activity.RESULT_OK) return
    when (requestCode) {
      ADD_SUBREDDIT_REQUEST_CODE -> {
        val subredditName = data?.getStringExtra(AddSubredditDialogFragment.SUBREDDIT_NAME_KEY)
        if (subredditName != null) {
          viewModel.add(subredditName)
        }
      }
    }
  }

  private inline fun onError(result: Result.Error, crossinline onHandled: () -> Unit) {
    @StringRes val stringRes = when (result) {
      Result.Error.ConnectionError -> R.string.no_connection
      Result.Error.NetworkError -> R.string.server_unreachable
    }
    val snackbar = binding?.root?.makeSnackbar(stringRes.toAndroidString())
      ?.setAnchorView(binding?.addSubredditButton)
    eventSnackbar.set(snackbar) { onHandled() }
  }

  private fun onSubredditsRefreshed(result: Result<Nothing>) {
    when (result) {
      is Result.Success.Empty -> Unit
      is Result.Error -> onError(result, viewModel.refreshedSubreddits::clear)
      else -> Timber.warn { "Refreshed subreddits result $result is not handled." }
    }
  }

  private fun onSubredditAdded(nameAndResult: Pair<String, Result<Subreddit>>) {
    val (name, result) = nameAndResult

    fun onSuccess(subreddit: Subreddit) {
      val snackbar = binding?.root?.makeSnackbar(
        getString(R.string.added_subreddit, subreddit.name.name).toAndroidString(),
        R.string.action_view.toAndroidString(),
        length = SnackbarLength.LONG
      ) {
        context?.let(subreddit.name.asUrl()::launch)
      }?.setAnchorView(binding?.addSubredditButton)
      eventSnackbar.set(snackbar, viewModel.addedSubreddit::clear)
    }

    fun onFailure(failure: Result.Failure) {
      @StringRes val stringRes = when (failure) {
        Result.Failure.NetworkFailure -> R.string.added_subreddit_does_not_exist
        Result.Failure.DatabaseFailure -> R.string.added_subreddit_exists
      }
      val string = getString(stringRes, name).toAndroidString()
      val snackbar = binding?.root?.makeSnackbar(string)?.setAnchorView(binding?.addSubredditButton)
      eventSnackbar.set(snackbar, viewModel.addedSubreddit::clear)
    }

    when (result) {
      is Result.Success -> onSuccess(result.data)
      is Result.Failure -> onFailure(result)
      is Result.Error -> onError(result, viewModel.addedSubreddit::clear)
      else -> Timber.warn { "Add subreddit result $result is not handled." }
    }
  }

  private fun onSubredditDeleted(result: Result<Subreddit>) {
    fun onSuccess(subreddit: Subreddit) {
      val snackbar = binding?.root?.makeSnackbar(
        getString(R.string.deleted_subreddit, subreddit.name.name).toAndroidString(),
        R.string.action_undo.toAndroidString(),
        length = SnackbarLength.LONG
      ) {
        viewModel.add(subreddit)
      }?.setAnchorView(binding?.addSubredditButton)
      eventSnackbar.set(snackbar, viewModel.deletedSubreddit::clear)
    }

    when (result) {
      is Result.Success -> onSuccess(result.data)
      else -> Timber.warn { "Delete subreddit result $result is not handled." }
    }
  }
}
