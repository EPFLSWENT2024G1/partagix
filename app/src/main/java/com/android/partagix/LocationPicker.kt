package com.android.partagix

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.partagix.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class LocationPicker : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private lateinit var map: GoogleMap
    private var selectedLocationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val selectLocationButton: Button = findViewById(R.id.select_location_button)
        selectLocationButton.setOnClickListener {
            LocationSelectionManager.selectedLocation?.let { selectedLocation ->
                val address = getAddress(selectedLocation.latitude, selectedLocation.longitude)
                Toast.makeText(this, "Selected Address: $address", Toast.LENGTH_SHORT).show()

                // Pass the location and address back to the previous activity
                val data = Intent().apply {
                    putExtra("latitude", selectedLocation.latitude)
                    putExtra("longitude", selectedLocation.longitude)
                    putExtra("address", address)
                }
                setResult(RESULT_OK, data)
                finish()
            } ?: run {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerDragListener(this)

        LocationSelectionManager.selectedLocation?.let { location ->
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        } ?: run {
            // Get the user's last known location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // User's location available, move camera to user's location
                        val userLocation = LatLng(location.latitude, location.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                    } else {
                        // User's location unavailable, move camera to default location (Lausanne, Switzerland)
                        val defaultLocation = LatLng(46.519962, 6.633597) // Lausanne, Switzerland
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
                    }
                }
                .addOnFailureListener { e ->
                    // Failed to get user's location, move camera to default location (Lausanne, Switzerland)
                    val defaultLocation = LatLng(46.519962, 6.633597) // Lausanne, Switzerland
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
                    e.printStackTrace()
                }
        }

        // Add a click listener to the map
        map.setOnMapClickListener { latLng ->
            selectedLocationMarker?.remove() // Remove existing marker
            selectedLocationMarker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .draggable(true)
            )
            println("------------------1")
            println("--------------------latLng: $latLng")
            println("--------------------adresse: ${getAddress(latLng.latitude, latLng.longitude)}")
            LocationSelectionManager.selectedLocation = latLng
            LocationSelectionManager.selectedAddress = getAddress(latLng.latitude, latLng.longitude)
            println("--------------------selectedLocation: ${LocationSelectionManager.selectedLocation}")
            println("--------------------selectedAddress: ${LocationSelectionManager.selectedAddress}")
        }
    }

    override fun onMarkerDragStart(marker: Marker) {
        println("------------------4")
        // Not needed
    }

    override fun onMarkerDrag(marker: Marker) {
        println("------------------3")
        // Not needed
    }

    override fun onMarkerDragEnd(marker: Marker) {
        println("------------------2")
        marker.position?.let { latLng ->
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            LocationSelectionManager.selectedLocation = latLng

            // Convert LatLng to human-readable address
            val geocoder = Geocoder(this)
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val addressText = address.getAddressLine(0) // Get the first address line
                    // Update the selected location with the human-readable address
                    LocationSelectionManager.selectedAddress = addressText
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this)
        val addresses: List<android.location.Address>?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                addressText = address.getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return addressText
    }
}


object LocationSelectionManager {
    var selectedLocation: LatLng? = null
    var selectedAddress: String? = "Unknown Adress"
}
