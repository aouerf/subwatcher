package com.aouerfelli.subwatcher.ui.main

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.transform.CircleCropTransformation
import com.aouerfelli.subwatcher.R
import com.aouerfelli.subwatcher.database.Subreddit
import com.aouerfelli.subwatcher.databinding.SubredditItemBinding
import com.aouerfelli.subwatcher.repository.asUri
import com.aouerfelli.subwatcher.util.extensions.layoutInflater
import com.aouerfelli.subwatcher.util.extensions.load

class SubredditListAdapter(
  private val imageLoader: ImageLoader,
  private val itemClickCallback: (Subreddit, Context) -> Unit
) : ListAdapter<Subreddit, SubredditListAdapter.ViewHolder>(diffItemCallback) {

  companion object {
    private val diffItemCallback = object : DiffUtil.ItemCallback<Subreddit>() {
      override fun areItemsTheSame(oldItem: Subreddit, newItem: Subreddit): Boolean {
        return oldItem.name == newItem.name
      }

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
      itemClickCallback(subreddit, v.context)
    }
  }
}
