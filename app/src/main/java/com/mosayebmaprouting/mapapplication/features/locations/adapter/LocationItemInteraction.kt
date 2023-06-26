package com.mosayebmaprouting.mapapplication.features.locations.adapter

import com.mosayebmaprouting.mapapplication.features.locations.domain.model.LocationModel

interface LocationItemInteraction {
    fun markerItemOnclick(location: LocationModel)
}