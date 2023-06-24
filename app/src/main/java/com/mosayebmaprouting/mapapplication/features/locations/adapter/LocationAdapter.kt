package com.mosayebmaprouting.mapapplication.features.locations.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mosayebmaprouting.mapapplication.databinding.RowBinding
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel
import javax.inject.Inject

class LocationAdapter @Inject constructor(): ListAdapter<LocationModel, LocationViewHolder>(MyDiffUtil()){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = RowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model)
        holder.setOnClickListener(listener , model)
    }

    class MyDiffUtil: DiffUtil.ItemCallback<LocationModel>() {
        override fun areItemsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private var listener: LocationItemInteraction? = null
    fun setListener(listener: LocationItemInteraction) {
        this.listener = listener
    }

}
