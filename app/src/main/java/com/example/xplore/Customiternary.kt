package com.example.xplore

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xplore.RetrofitClient.retrofitService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

private const val API_KEY = BuildConfig.MapsAPI
private const val LOCATION_PERMISSION_REQUEST_CODE = 1

class Customiternary : Fragment(),OnItemClickListener {

    private lateinit var startDateInput: EditText
    private lateinit var mainpart:LinearLayout
    private lateinit var endDateInput: EditText
    private lateinit var createItineraryButton: Button
    private lateinit var categoryHistorical: CheckBox
    private lateinit var categoryArts: CheckBox
    private lateinit var categoryShopping: CheckBox
    private lateinit var categoryBeaches: CheckBox
    private lateinit var placesRecyclerView: RecyclerView

    private lateinit var adapter: PlaceAdapterforcustom
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.custom_iternary, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupDateTimePickers(view)
        startDateInput = view.findViewById(R.id.startDateInput)
        endDateInput = view.findViewById(R.id.endDateInput)
        mainpart = view.findViewById(R.id.formLayout)
        createItineraryButton = view.findViewById(R.id.createItineraryButton)

        categoryHistorical = view.findViewById(R.id.categoryHistorical)
        categoryArts = view.findViewById(R.id.categoryArts)
        categoryShopping = view.findViewById(R.id.categoryShopping)
        categoryBeaches = view.findViewById(R.id.categoryBeaches)

        placesRecyclerView = view.findViewById(R.id.itineraryRecyclerView)
        placesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PlaceAdapterforcustom(emptyList(),this)
        placesRecyclerView.adapter = adapter

        createItineraryButton.setOnClickListener {
            getCurrentLocation()
        }

        return view
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatLng = "${location.latitude},${location.longitude}"
                createItinerary(currentLatLng)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Unable to get current location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDateTimePickers(view: View) {
        startDateInput = view.findViewById(R.id.startDateInput)
        endDateInput = view.findViewById(R.id.endDateInput)

        startDateInput.setOnClickListener {
            showDatePicker(startDateInput)
        }


        endDateInput.setOnClickListener {
            showDatePicker(endDateInput)
        }

    }

    private fun showDatePicker(editText: EditText) {
        val currentDate = calendar.timeInMillis
        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                editText.setText("${year}-${month + 1}-$dayOfMonth")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = currentDate
        datePickerDialog.show()
    }


    private fun createItinerary(currentLatLng: String) {
        val startDate = startDateInput.text.toString()
        val endDate = endDateInput.text.toString()

        val selectedCategories = mutableListOf<String>()
        if (categoryHistorical.isChecked) selectedCategories.add("tourist_attraction IN Mumbai (historical OR heritage OR monument OR UNESCO)")
        if (categoryArts.isChecked) selectedCategories.add("movie_theater OR art_gallery OR museum) IN Mumbai (theater OR cinema OR multiplex OR 'art gallery' OR exhibition OR 'art museum' OR performance OR 'live music')")
        if (categoryShopping.isChecked) selectedCategories.add("shopping_mall OR clothing_store OR shoe_store OR department_store OR jewelry_store OR electronics_store) IN Mumbai (shopping OR mall OR market OR 'street shopping' OR fashion)")
        if (categoryBeaches.isChecked) selectedCategories.add("tourist_attraction OR beach OR park) IN Mumbai (beach OR seaface OR coast OR promenade OR walkway OR waterfront OR 'sea view' OR chowpatty)")

        if (selectedCategories.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please select at least one category.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (startDate.isEmpty()  || endDate.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please fill in all date and time fields.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start = dateFormat.parse(startDate)!!
        val end = dateFormat.parse(endDate)!!
        val calendar = Calendar.getInstance()
        calendar.time = start
        val days = mutableListOf<Date>()
        while (!calendar.time.after(end)) {
            days.add(calendar.time)
            calendar.add(Calendar.DATE, 1)
        }

        val placesPerDay = 5
        val allPlaces = mutableListOf<Pair<Date, List<Place>>>()

        lifecycleScope.launch {
            for (day in days) {
                val placesForDay = mutableListOf<Place>()
                for (category in selectedCategories) {
                    val places = fetchPlacesByCategory(category, currentLatLng, placesPerDay)
                    placesForDay.addAll(places)
                }
                allPlaces.add(day to placesForDay)
            }

            displayItinerary(allPlaces)
        }
    }

    private suspend fun fetchPlacesByCategory(
        category: String,
        currentLatLng: String,
        placesPerDay: Int
    ): List<Place> {
        val places = mutableListOf<Place>()
        val response = withContext(Dispatchers.IO) {
            retrofitService.getNearbyPlacesByCategory(category, currentLatLng, 5000, API_KEY).execute()
        }
        if (response.isSuccessful) {
            response.body()?.results?.let { places.addAll(it.take(placesPerDay)) }
            places.forEach { place ->
                Log.d("Customiternary", "Place Name: ${place.name}")
            }
        } else {
            Log.e(
                "Customiternary",
                "Error fetching places for category $category: ${response.errorBody()?.string()}"
            )
        }
        return places
    }

    private fun displayItinerary(itinerary: List<Pair<Date, List<Place>>>) {
        adapter = PlaceAdapterforcustom(itinerary,this)
        placesRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        mainpart.visibility = View.GONE
        placesRecyclerView.visibility = View.VISIBLE
    }
    override fun onItemClick(placeId: String) {
        val intent = Intent(requireActivity(), ItineraryActivity::class.java)
        intent.putExtra("PLACE_ID", placeId)
        startActivity(intent)
    }

}
