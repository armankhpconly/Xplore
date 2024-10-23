package com.example.xplore

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.util.Log

import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.analytics.FirebaseAnalytics
import kotlin.math.log

class Home : Fragment() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
        private lateinit var fusedLocationClient: FusedLocationProviderClient
        private var currentPlaceId: String? = null
        val bundle  = Bundle()
    

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_home, container, false)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
            view.findViewById<CardView>(R.id.card1).setOnClickListener {
                Log.d("debug", "Card 1 clicked")
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "card1")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
                fetchCurrentLocationAndNavigate("ChIJnz0di8iw5zsRz-Ko2mndITY")
            }
            view.findViewById<CardView>(R.id.card2).setOnClickListener {
                Log.d("HomeFragment", "Card 2 clicked")
                fetchCurrentLocationAndNavigate("ChIJrVwNOsfR5zsRPHOcIKclCsc")
            }
            view.findViewById<CardView>(R.id.card3).setOnClickListener {
                Log.d("HomeFragment", "Card 3 clicked")
                fetchCurrentLocationAndNavigate("ChIJefVzk37O5zsRVbE1WoGalIU")
            }
            view.findViewById<CardView>(R.id.card4).setOnClickListener {
                Log.d("HomeFragment", "Card 4 clicked")
                fetchCurrentLocationAndNavigate("ChIJw-tR2MDO5zsRKWNSFGmHYcw")
            }
            view.findViewById<CardView>(R.id.history_card).setOnClickListener {
                openCategory("tourist_attraction IN Mumbai (historical OR heritage OR monument OR UNESCO)")
            }
            view.findViewById<CardView>(R.id.streetfood_card).setOnClickListener {
                openCategory(category = "(restaurant OR food) IN Mumbai (street food OR fast food OR snacks OR market OR bazaar OR chowpatty OR khau galli OR food street OR local cuisine OR 'Mumbai specialities')")
            }
            view.findViewById<CardView>(R.id.beaches_card).setOnClickListener {
                openCategory(category = "(tourist_attraction OR beach OR park) IN Mumbai (beach OR seaface OR coast OR promenade OR walkway OR waterfront OR 'sea view' OR chowpatty)")
            }
            view.findViewById<CardView>(R.id.nature_card).setOnClickListener {
                openCategory(category = "(park OR zoo OR natural_feature) IN Mumbai (park OR garden OR nature OR green space OR lake)")
            }
            view.findViewById<CardView>(R.id.art_card).setOnClickListener {
                openCategory(category = "(movie_theater OR art_gallery OR museum) IN Mumbai (theater OR cinema OR multiplex OR 'art gallery' OR exhibition OR 'art museum' OR performance OR 'live music')")
            }
            view.findViewById<CardView>(R.id.shopping_card).setOnClickListener {
                openCategory(category = "(shopping_mall OR clothing_store OR shoe_store OR department_store OR jewelry_store OR electronics_store) IN Mumbai (shopping OR mall OR market OR 'street shopping' OR fashion)")
            }

            return view
        }

        private fun fetchCurrentLocationAndNavigate(placeId: String) {
            currentPlaceId = placeId
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("HomeFragment", "Requesting location permissions")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                Log.d("HomeFragment", "Permissions already granted, fetching location")
                fusedLocationClient.lastLocation
                    .addOnSuccessListener(requireActivity()) { location: Location? ->
                        if (location != null) {
                            Log.d("HomeFragment", "Location fetched: ${location.latitude}, ${location.longitude}")
                            val origin = "${location.latitude},${location.longitude}"
                            navigateToItinerary(placeId, origin)
                        } else {
                            Log.d("HomeFragment", "Failed to fetch location")
                        }
                    }
            }
        }

        private fun navigateToItinerary(placeId: String, origin: String) {
            Log.d("HomeFragment", "Navigating to Itinerary with placeId: $placeId and origin: $origin")
            val intent = Intent(requireActivity(), ItineraryActivity::class.java).apply {
                putExtra("PLACE_ID", placeId)
                putExtra("ORIGIN", origin)
            }
            startActivity(intent)
        }

        companion object {
            private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        }
    private fun openCategory(category: String) {
        bundle.putString("category", category);
        Log.d("HomeFragment", "Opening category: $category")
        Toast.makeText(this.context, "you click on $category", Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, CategoryActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }
    }
