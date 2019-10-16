package io.github.aouerfelli.subwatcher.ui.main

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import io.github.aouerfelli.subwatcher.Subreddit
import io.github.aouerfelli.subwatcher.databinding.SubredditItemBinding
import io.github.aouerfelli.subwatcher.util.layoutInflater
import io.github.aouerfelli.subwatcher.util.toBitmap

class SubredditListAdapter : ListAdapter<Subreddit, SubredditListAdapter.ViewHolder>(diffCallback) {

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

    class ViewHolder(private val itemBinding: SubredditItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        companion object {
            private val customTabsIntent = CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .enableUrlBarHiding()
                .setShowTitle(true)
                .build()
        }

        var item: Subreddit? = null
            set(value) {
                field = value
                value ?: return
                itemBinding.name.text = value.name.value
                itemBinding.icon.load(value.iconImage?.toBitmap()) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
            }

        init {
            itemBinding.root.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val item = item ?: return
            customTabsIntent.launchUrl(v.context, item.name.asUrl())
        }
    }
}
