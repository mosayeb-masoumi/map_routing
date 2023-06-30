package com.mosayebmaprouting.mapapplication.features.locations.data.neshan_repository_impl


import android.widget.Toast
import com.mosayebmaprouting.mapapplication.features.locations.data.data_source.NeshanService
import com.mosayebmaprouting.mapapplication.features.locations.domain.repository.NeshanDirectionRepository
import org.neshan.common.utils.PolylineEncoding
import org.neshan.mapsdk.model.Marker
import org.neshan.mapsdk.model.Polyline
import org.neshan.servicessdk.direction.NeshanDirection
import org.neshan.servicessdk.direction.model.NeshanDirectionResult
import org.neshan.servicessdk.direction.model.Route
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class NeshanDirectionRepositoryImpl @Inject constructor() : NeshanDirectionRepository {


    var result: NeshanDirectionResult? = null

    override suspend fun getNeshanDirection(
        firstMarker: Marker,
        secondMarker: Marker
    ): NeshanDirectionResult {


        NeshanDirection.Builder(
            "service.1086dbd8409845568a76f21114606dae",
            firstMarker.latLng,
            secondMarker.latLng
        ).build().call(object : Callback<NeshanDirectionResult?> {
            override fun onResponse(
                call: Call<NeshanDirectionResult?>,
                response: Response<NeshanDirectionResult?>
            ) {

                // two type of routing
                if (response.body() != null && response.body()!!.routes != null &&
                    !response.body()!!.routes.isEmpty()
                ) {
                    result = response.body()!!
                } else {
//                    Toast.makeText(context, "مسیری یافت نشد", Toast.LENGTH_LONG)
//                        .show()
                    result = null
                }
            }

            override fun onFailure(call: Call<NeshanDirectionResult?>, t: Throwable) {
                result = null
            }
        })

        return result!!
    }


}
