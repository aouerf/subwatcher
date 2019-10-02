package io.github.aouerfelli.subwatcher.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import io.github.aouerfelli.subwatcher.databinding.SubredditItemBinding
import io.github.aouerfelli.subwatcher.repository.Subreddit
import io.github.aouerfelli.subwatcher.util.toBitmap

class SubredditListAdapter : ListAdapter<Subreddit, SubredditListAdapter.ViewHolder>(diffCallback) {

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Subreddit>() {

            override fun areItemsTheSame(oldItem: Subreddit, newItem: Subreddit): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Subreddit, newItem: Subreddit): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = SubredditItemBinding.inflate(inflater, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(private val itemBinding: SubredditItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: Subreddit) {
            itemBinding.name.text = item.name.value
            itemBinding.icon.load(item.iconImage?.toBitmap()) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }
    }
}
