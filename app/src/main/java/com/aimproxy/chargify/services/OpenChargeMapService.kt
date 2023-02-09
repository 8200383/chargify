package com.aimproxy.chargify.services

import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class EvStation(
    @SerializedName("OperatorInfo") var OperatorInfo: OperatorInfo? = null,
    @SerializedName("StatusType") var StatusType: StatusType? = null,
    @SerializedName("UsageCost") var UsageCost: String? = null,
    @SerializedName("AddressInfo") var AddressInfo: AddressInfo? = null,
    @SerializedName("Connections") val Connections: ArrayList<Connections>? = null,
    @SerializedName("NumberOfPoints") var NumberOfPoints: Int? = null,
)

data class OperatorInfo(
    @SerializedName("PhonePrimaryContact") val PhonePrimaryContact: String? = null,
    @SerializedName("ContactEmail") var ContactEmail: String? = null,
    @SerializedName("Title") var Title: String? = null
)

data class StatusType(
    @SerializedName("IsOperational") val IsOperational: Boolean? = null,
)

data class AddressInfo(
    @SerializedName("Title") var Title: String? = null,
    @SerializedName("Town") var Town: String? = null,
    @SerializedName("Latitude") var Latitude: Double? = null,
    @SerializedName("Longitude") var Longitude: Double? = null,
    @SerializedName("Distance") var Distance: Double? = null,
    @SerializedName("DistanceUnit") var DistanceUnit: Int? = null
)

data class ConnectionType(
    @SerializedName("FormalName") var FormalName: String? = null,
    @SerializedName("Title") var Title: String? = null
)

data class Connections(
    @SerializedName("ConnectionType") var ConnectionType: ConnectionType? = ConnectionType(),
    @SerializedName("StatusType") var StatusType: StatusType? = StatusType(),
    @SerializedName("Amps") var Amps: Int? = null,
    @SerializedName("Voltage") var Voltage: Int? = null,
    @SerializedName("PowerKW") var PowerKW: Double? = null,
    @SerializedName("Quantity") var Quantity: Int? = null
)

interface OpenChargeMapRequests {
    @GET("poi/?key=${OPEN_CHARGE_MAP_API_KEY}&output=json")
    fun getEvStations(
        @Query("maxresults") maxResults: Number,
        @Query("countrycode") countryCode: String,
        @Query("latitude") latitude: Number,
        @Query("longitude") longitude: Number
    ): Call<List<EvStation>>
}

data class SearchEvStationsNearbyInput(
    val maxResults: Int,
    val countryCode: String,
    val latitude: Double,
    val longitude: Double
)

private const val OPEN_CHARGE_MAP_API = "https://api.openchargemap.io/v3/"
private const val OPEN_CHARGE_MAP_API_KEY = "6babce48-0973-49af-937b-3f3a1c76ae30"

class OpenChargeMapService {
    private val retrofit = Retrofit.Builder()
        .baseUrl(OPEN_CHARGE_MAP_API)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: OpenChargeMapRequests = retrofit.create(OpenChargeMapRequests::class.java)

    fun lookupEvStations(
        input: SearchEvStationsNearbyInput,
        callback: (data: List<EvStation>?, error: Throwable?) -> Unit
    ) {
        service.getEvStations(
            maxResults = input.maxResults,
            countryCode = input.countryCode,
            latitude = input.latitude,
            longitude = input.longitude
        ).enqueue(object : Callback<List<EvStation>> {
            override fun onResponse(
                call: Call<List<EvStation>>,
                response: Response<List<EvStation>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, Throwable("NetworkError"))
                }

                Log.d("OpenChargeMap#isSuccessful", response.isSuccessful.toString())
            }

            override fun onFailure(call: Call<List<EvStation>>, t: Throwable) {
                Log.d("OpenChargeMap#onFailure", t.message, t)
                callback(null, t)
            }
        })
    }
}