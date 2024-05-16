package com.android.partagix.model.location

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

data class Location(val latitude: Double, val longitude: Double, val locationName: String)

fun sendRequest(url: String, callback: Callback): Call {
  val client = OkHttpClient()
  val request = Request.Builder().url(url).build()

  val call = client.newCall(request)
  call.enqueue(callback)
  return call
}
