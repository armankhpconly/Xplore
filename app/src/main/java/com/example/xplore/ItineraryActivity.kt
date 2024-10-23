package com.example.xplore

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItineraryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewPager: ViewPager
    private lateinit var destinationName: TextView
    private lateinit var travelModesRadioGroup: RadioGroup
    private lateinit var directionsRecyclerView: RecyclerView

    private lateinit var placeId: String
    private lateinit var origin: LatLng // Updated to store origin as LatLng

    private val retrofitService = RetrofitClient.retrofitService
    private var polyline: Polyline? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var isMapReady = false
    private var placeDetails: ApiPlaceDetailsResult? = null
    private lateinit var googleMap: GoogleMap // Move this line after isMapReady flag

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iternary)

        // Initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get placeId and origin from intent
        placeId = intent.getStringExtra("PLACE_ID") ?: ""
        // Assuming origin is passed as latitude and longitude
        val originLat = intent.getDoubleExtra("ORIGIN_LAT", 0.0)
        val originLng = intent.getDoubleExtra("ORIGIN_LNG", 0.0)
        origin = LatLng(originLat, originLng)

        // Initialize views
        viewPager = findViewById(R.id.viewPager)
        destinationName = findViewById(R.id.destinationName)
        travelModesRadioGroup = findViewById(R.id.travelModesRadioGroup)
        directionsRecyclerView = findViewById(R.id.directionsRecyclerView)

        // Set up Google Map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Fetch place details
        fetchPlaceDetails(placeId)

        // Set up RecyclerView for directions
        directionsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        isMapReady = true

        // Add markers for origin and destination if place details are already fetched
        placeDetails?.let { updateUI(it) }

        // Add markers for origin
        addMarkersToMap()
    }

    private fun fetchPlaceDetails(placeId: String) {
        val call = retrofitService.getPlaceDetails(placeId, "name,formatted_address,photos,geometry", API_KEY)
        call.enqueue(object : Callback<ApiPlaceDetailsResponse> {
            override fun onResponse(call: Call<ApiPlaceDetailsResponse>, response: Response<ApiPlaceDetailsResponse>) {
                if (response.isSuccessful) {
                    val placeDetails = response.body()?.result
                    placeDetails?.let {
                        updateUI(it)
                    } ?: run {
                        Toast.makeText(this@ItineraryActivity, "No place details found", Toast.LENGTH_SHORT).show()
                        Log.e("ItineraryActivity", "No place details found in response: ${response.body()}")
                    }
                } else {
                    Toast.makeText(this@ItineraryActivity, "Failed to fetch place details", Toast.LENGTH_SHORT).show()
                    Log.e("ItineraryActivity", "Failed response: ${response.errorBody()?.string()}")
                    Log.e("ItineraryActivity", "Response code: ${response.code()}")
                    Log.e("ItineraryActivity", "Response message: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiPlaceDetailsResponse>, t: Throwable) {
                Toast.makeText(this@ItineraryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("ItineraryActivity", "API call failed", t)
            }
        })
    }

    private fun updateUI(details: ApiPlaceDetailsResult) {
        placeDetails = details

        // If map is not ready, return and wait for onMapReady to call updateUI
        if (!isMapReady) return

        // Update destination name
        destinationName.text = details.name

        // Update images in ViewPager
        val imageUrls = details.photos?.map { photo -> getPhotoUrl(photo.photoReference ?: "") } ?: emptyList()
        val adapter = ImagePagerAdapter(this, imageUrls)
        viewPager.adapter = adapter

        // Update map with destination location
        details.geometry?.location?.let { location ->
            val destinationLatLng = LatLng(location.lat ?: 0.0, location.lng ?: 0.0)
            googleMap.addMarker(MarkerOptions().position(destinationLatLng).title(details.name))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 15f))

            // Set up travel modes radio group listener
            travelModesRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                val selectedRadioButton = findViewById<RadioButton>(checkedId)
                val selectedMode = selectedRadioButton?.text.toString().lowercase()
                fetchDirections(destinationLatLng, selectedMode)
            }
        } ?: run {
            Toast.makeText(this, "Location data is missing", Toast.LENGTH_SHORT).show()
            Log.e("ItineraryActivity", "Geometry or location data is null")
        }
    }

    private fun fetchDirections(destinationLatLng: LatLng, travelMode: String) {
        // Get user's current location
        getCurrentLocation { currentLocation ->
            currentLocation?.let { currentLatLng ->
                val originLatLng = LatLng(currentLatLng.latitude, currentLatLng.longitude)
                val originString = "${originLatLng.latitude},${originLatLng.longitude}"
                val destinationString = "${destinationLatLng.latitude},${destinationLatLng.longitude}"

                val call = retrofitService.getDirections(originString, destinationString, travelMode, API_KEY)
                call.enqueue(object : Callback<DirectionsResponse> {
                    override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                        if (response.isSuccessful) {
                            val directions = response.body()?.routes?.firstOrNull()
                            directions?.let {
                                drawRoute(it)
                                showDirectionsList(it)
                            } ?: run {
                                Log.e("ItineraryActivity", "No routes found in directions response")
                            }
                        } else {
                            Toast.makeText(this@ItineraryActivity, "Failed to fetch directions", Toast.LENGTH_SHORT).show()
                            Log.e("ItineraryActivity", "Failed response: ${response.errorBody()?.string()}")
                            Log.e("ItineraryActivity", "Response code: ${response.code()}")
                            Log.e("ItineraryActivity", "Response message: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                        Toast.makeText(this@ItineraryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("ItineraryActivity", "Failed to fetch directions", t)
                    }
                })
            } ?: run {
                Toast.makeText(this@ItineraryActivity, "Failed to get current location", Toast.LENGTH_SHORT).show()
                Log.e("ItineraryActivity", "Current location is null")
            }
        }
    }

    private fun getCurrentLocation(callback: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                callback(location)
            }.addOnFailureListener { exception ->
                callback(null)
                Log.e("ItineraryActivity", "Failed to get location: ${exception.message}", exception)
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
            callback(null)
        }
    }

    private fun drawRoute(route: Route) {
        val polylineOptions = PolylineOptions()
            .color(Color.BLUE)
            .width(10f)

        val boundsBuilder = LatLngBounds.Builder() // Create a bounds builder

        route.legs?.forEach { leg ->
            leg.steps?.forEach { step ->
                step.polyline.points.let {
                    val decodedPath = PolyUtil.decode(it)
                    polylineOptions.addAll(decodedPath)
                    decodedPath.forEach { latLng -> boundsBuilder.include(latLng) } // Add each point to bounds builder
                }
            }
        }

        // Remove any previously drawn polyline
        polyline?.remove()

        // Draw new polyline on the map
        polyline = googleMap.addPolyline(polylineOptions)

        // Adjust the camera to fit the polyline
        val bounds = boundsBuilder.build()
        val padding = 100 // padding around the route (in pixels)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }

    private fun showDirectionsList(route: Route) {
        val directionsAdapter = DirectionsAdapter(route.legs?.flatMap { it.steps!! } ?: emptyList())
        directionsRecyclerView.adapter = directionsAdapter
        directionsRecyclerView.visibility = RecyclerView.VISIBLE
    }

    private fun addMarkersToMap() {
        // Add marker for origin (current location)
        getCurrentLocation { currentLocation ->
            currentLocation?.let {
                val originLatLng = LatLng(it.latitude, it.longitude)
                googleMap.addMarker(MarkerOptions().position(originLatLng).title("You are here"))

                // Marker for destination will be added in updateUI function
            } ?: run {
                Toast.makeText(this, "Failed to get current location", Toast.LENGTH_SHORT).show()
                Log.e("ItineraryActivity", "Current location is null")
            }
        }
    }

    private fun getPhotoUrl(photoReference: String): String {
        return "https://maps.googleapis.com/maps/api/place/photo" +
                "?maxwidth=400" +
                "&photoreference=${photoReference}" +
                "&key=${API_KEY}"
    }

    companion object {
        private const val API_KEY = BuildConfig.MapsAPI
        private const val REQUEST_LOCATION_PERMISSION = 1001
    }
}
