package com.mosayebmaprouting.mapapplication.features.locations.domain.repository


import org.neshan.mapsdk.model.Marker
import org.neshan.servicessdk.direction.model.NeshanDirectionResult

interface NeshanDirectionRepository {
    suspend fun getNeshanDirection(firstMarker: Marker, secondMarker: Marker): NeshanDirectionResult
}