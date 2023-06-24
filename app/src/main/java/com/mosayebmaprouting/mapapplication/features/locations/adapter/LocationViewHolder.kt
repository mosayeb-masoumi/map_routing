package com.mosayebmaprouting.mapapplication.features.locations.adapter

import androidx.recyclerview.widget.RecyclerView
import com.mosayebmaprouting.mapapplication.databinding.RowBinding
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel

class LocationViewHolder (private val binding: RowBinding): RecyclerView.ViewHolder(binding.root)  {

    fun bind(model: LocationModel?) {

        binding.txtAddress.text= model?.address

    }

    fun setOnClickListener(listener: LocationItemInteraction?, model: LocationModel) {

        binding.root.setOnClickListener {
            model.let {
//                listener?.markerItemOnclick(it)
            }
        }


        binding.imgDelete.setOnClickListener {
            model.let {
                listener?.deleteItemOnclick(it)
            }
        }
    }
}