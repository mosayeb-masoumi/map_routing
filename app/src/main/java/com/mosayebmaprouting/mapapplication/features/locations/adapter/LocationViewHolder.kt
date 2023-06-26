package com.mosayebmaprouting.mapapplication.features.locations.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.mosayebmaprouting.mapapplication.R
import com.mosayebmaprouting.mapapplication.databinding.RowBinding
import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel

class LocationViewHolder (private val binding: RowBinding): RecyclerView.ViewHolder(binding.root)  {

    @SuppressLint("SetTextI18n")
    fun bind(model: LocationModel?) {
        model?.let{
            if(model.address.isEmpty()){
                binding.txtAddress.text= itemView.context.resources.getString(R.string.no_address_found)
            }else{
                binding.txtAddress.text= model.address
            }

            binding.txtLat.text = "lat: ${model.lat}"
            binding.txtLng.text = "lng: ${model.lng}"
        }


    }

    fun setOnClickListener(listener: LocationItemInteraction?, model: LocationModel) {

        binding.root.setOnClickListener {
            model.let {
                listener?.markerItemOnclick(it)
            }
        }

    }
}