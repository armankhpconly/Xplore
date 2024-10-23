// CategoryActivity.kt
package com.example.xplore

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class CategoryActivity : AppCompatActivity() {

    private lateinit var category: String
    private lateinit var placesRecyclerView: RecyclerView
    private lateinit var placesAdapter: PlaceAdapter
    private lateinit var toggleButton: SwitchCompat
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actvity_category)

        category = intent.getStringExtra("category") ?: ""

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = category.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        placesRecyclerView = findViewById(R.id.placesRecyclerView)
        placesRecyclerView.layoutManager = LinearLayoutManager(this)
        placesAdapter = PlaceAdapter { place ->
            navigateToItinerary(place)
        }
        placesRecyclerView.adapter = placesAdapter

        toggleButton = findViewById(R.id.toggleButton)
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                loadNearbyPlaces()
            } else {
                loadMumbaiPlaces()
            }
        }

        loadMumbaiPlaces()
    }

    private fun loadMumbaiPlaces() {
        RetrofitClient.retrofitService.getPlacesByCategory(category, "mumbai", API_KEY)
            .enqueue(object : Callback<PlacesResponse> {
                override fun onResponse(call: Call<PlacesResponse>, response: Response<PlacesResponse>) {
                    if (response.isSuccessful) {
                        placesAdapter.setPlaces(response.body()?.results ?: emptyList())
                    } else {
                        Toast.makeText(this@CategoryActivity, "Failed to load places", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PlacesResponse>, t: Throwable) {
                    Toast.makeText(this@CategoryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadNearbyPlaces() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
            if (location != null) {
                val locationString = "${location.latitude},${location.longitude}"
                RetrofitClient.retrofitService.getNearbyPlacesByCategory(category, locationString, 5000,BuildConfig.MapsAPI)
                    .enqueue(object : Callback<PlacesResponse> {
                        override fun onResponse(call: Call<PlacesResponse>, response: Response<PlacesResponse>) {
                            if (response.isSuccessful) {
                                val places = response.body()?.results ?: emptyList()
                                Log.d("CategoryActivity", "Nearby places loaded: ${places.size}")
                                placesAdapter.setPlaces(places)
                            } else {
                                Log.e("CategoryActivity", "Failed to load nearby places: ${response.errorBody()?.string()}")
                                Toast.makeText(this@CategoryActivity, "Failed to load nearby places", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<PlacesResponse>, t: Throwable) {
                            Log.e("CategoryActivity", "Error: ${t.message}")
                            Toast.makeText(this@CategoryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Log.e("CategoryActivity", "Location is null")
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun navigateToItinerary(place: Place) {
        val intent = Intent(this, ItineraryActivity::class.java).apply {
            putExtra("PLACE_ID", place.placeId)
            putExtra("ORIGIN", "${currentLocation?.latitude},${currentLocation?.longitude}")
        }
        startActivity(intent)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val API_KEY = BuildConfig.MapsAPI
    }
}
