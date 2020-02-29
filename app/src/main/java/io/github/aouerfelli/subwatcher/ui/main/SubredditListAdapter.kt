package io.github.aouerfelli.subwatcher.ui.main

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.transform.CircleCropTransformation
import io.github.aouerfelli.subwatcher.R
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.databinding.SubredditItemBinding
import io.github.aouerfelli.subwatcher.repository.asUri
import io.github.aouerfelli.subwatcher.util.extensions.layoutInflater
import io.github.aouerfelli.subwatcher.util.extensions.load
import io.github.aouerfelli.subwatcher.work.newposts.ViewSubredditBroadcastReceiver

class SubredditListAdapter(private val imageLoader: ImageLoader) :
  ListAdapter<Subreddit, SubredditListAdapter.ViewHolder>(diffCallback) {

  companion object {
    private val diffCallback = object : DiffUtil.ItemCallback<Subreddit>() {
      override fun areItemsTheSame(oldItem: Subreddit, newItem: Subreddit): Boolean {
        return oldItem.name == newItem.name
      }

      @SuppressLint("DiffUtilEquals")
      override fun areContentsTheSame(oldItem: Subreddit, newItem: Subreddit): Boolean {
        return oldItem == newItem
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val itemBinding = SubredditItemBinding.inflate(parent.context.layoutInflater, parent, false)
    return ViewHolder(itemBinding)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.item = getItem(position)
  }

  inner class ViewHolder(private val itemBinding: SubredditItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

    var item: Subreddit? = null
      set(value) {
        field = value
        value ?: return
        itemBinding.name.text = value.name.name
        itemBinding.icon.load(value.iconUrl?.asUri(), imageLoader) {
          crossfade(true)
          transformations(CircleCropTransformation())
          fallback(R.drawable.ic_reddit_mark)
        }
      }

    init {
      itemBinding.root.setOnClickListener(this)
    }

    override fun onClick(v: View) {
      val subreddit = item ?: return
      // TODO: Do not use BroadcastReceiver for this as this will start a new task
      val intent = ViewSubredditBroadcastReceiver.createIntent(v.context, subreddit.name)
      v.context.sendBroadcast(intent)
    }
  }
}
