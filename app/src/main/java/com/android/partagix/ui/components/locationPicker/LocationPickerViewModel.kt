package com.android.partagix.ui.components.locationPicker

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.core.os.bundleOf
import com.android.partagix.model.location.Location
import com.android.partagix.model.location.sendRequest
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response

class LocationPickerViewModel {

  fun getLocation(query: String, locationState: MutableState<Location?>) {
    val url = "https://nominatim.openstreetmap.org/search?q=$query&format=json"
    sendRequest(
        url,
        object : Callback {
          override fun onFailure(call: Call, e: IOException) {
            Log.e("OverviewViewModel", "Failed to get the location", e)
          }

          override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful || response.body == null) {
              Log.e("OverviewViewModel", "Failed to get the location: $response")
            } else {
              val res = response.body!!.charStream()
              val response = Gson().fromJson(res, Array<NomatismResponse>::class.java).firstOrNull()
              if (response != null) {
                val location =
                    Location(
                        response.lat.toDouble(),
                        response.lon.toDouble(),
                        parseDisplayName(response.displayName))
                locationState.value = location
              }
            }
          }
        })
  }

  fun ourLocationToAndroidLocation(location: Location?): android.location.Location {
    if (location == null) {
      return android.location.Location("")
    }
    val androidLocation = android.location.Location("")
    androidLocation.latitude = location.latitude
    androidLocation.longitude = location.longitude
    androidLocation.extras = bundleOf("display_name" to location.locationName)
    return androidLocation
  }
}

fun parseDisplayName(displayName: String): String {
  val parts = displayName.split(", ")
  val country = parts.lastOrNull()
  val town = parts[0].split(", ").last()
  val countryCode =
      if (country != null && parts.size >= 2) {
        parts[parts.size - 2]
      } else {
        parts.last()
      }
  if (countryCode == town || countryCode.contains(country ?: "", ignoreCase = true)) {
    return "$town, $country"
  }

  return "$town, $countryCode $country"
}

data class NomatismResponse(
    val lat: String,
    val lon: String,
    @SerializedName("display_name") val displayName: String
)
