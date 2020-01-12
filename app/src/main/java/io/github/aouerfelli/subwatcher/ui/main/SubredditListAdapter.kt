package io.github.aouerfelli.subwatcher.ui.main

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.transform.CircleCropTransformation
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.databinding.SubredditItemBinding
import io.github.aouerfelli.subwatcher.util.launch
import io.github.aouerfelli.subwatcher.util.layoutInflater
import io.github.aouerfelli.subwatcher.util.load
import io.github.aouerfelli.subwatcher.util.toBitmap

class SubredditListAdapter(private val imageLoader: ImageLoader) :
  ListAdapter<Subreddit, SubredditListAdapter.ViewHolder>(diffCallback) {

  companion object {
    private val diffCallback = object : DiffUtil.ItemCallback<Subreddit>() {
      override fun areItemsTheSame(oldItem: Subreddit, newItem: Subreddit): Boolean {
        return oldItem.id == newItem.id
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
        itemBinding.name.text = value.name.value
        itemBinding.icon.load(value.iconImage?.toBitmap(), imageLoader) {
          crossfade(true)
          transformations(CircleCropTransformation())
          // TODO: fallback()
        }
      }

    init {
      itemBinding.root.setOnClickListener(this)
    }

    override fun onClick(v: View) {
      item?.name?.asUrl()?.launch(v.context)
    }
  }
}
