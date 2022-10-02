package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.ItemBinding

class AsteroidListAdapter(private val clickListener: AsteroidClickListener): ListAdapter<Asteroid, AsteroidListAdapter.AsteroidListViewHolder>(DiffCallback){
    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem == newItem
        }
    }

    class AsteroidListViewHolder(private var binding: ItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: AsteroidClickListener, asteroid: Asteroid) {
             binding.asteroid = asteroid
             binding.clickListener = listener
             binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AsteroidListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBinding.inflate(layoutInflater, parent, false)
                return AsteroidListViewHolder(binding)
            }
        }
    }

    /**
     * Part of the RecyclerView adapter, called when RecyclerView needs a new [ViewHolder].
     *
     * A ViewHolder holds a view for the [RecyclerView] as well as providing additional information
     * to the RecyclerView such as where on the screen it was last drawn during scrolling.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidListViewHolder {
        return AsteroidListViewHolder.from(parent)

    }

    /**
     * Part of the RecyclerView adapter, called when RecyclerView needs to show an item.
     *
     * The ViewHolder passed may be recycled, so make sure that this sets any properties that
     * may have been set previously.
     */
    override fun onBindViewHolder(holder: AsteroidListViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }
}

class AsteroidClickListener(val clickListener: (chapter: Asteroid) -> Unit) {
    fun onClick(chapter: Asteroid) = clickListener(chapter)
}