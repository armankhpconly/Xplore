package com.example.xplore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.BuildConfig
import java.text.SimpleDateFormat
import java.util.*
private const val API_KEY = com.example.xplore.BuildConfig.MapsAPI

public interface OnItemClickListener {
    fun onItemClick(placeId: String)
}
class PlaceAdapterforcustom(private val placesByDay: List<Pair<Date, List<Place>>>,
                            private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<PlaceAdapterforcustom.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        val placesRecyclerView: RecyclerView = itemView.findViewById(R.id.placesRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_places, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (day, places) = placesByDay[position]
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        holder.dayTextView.text = dateFormat.format(day)

        val layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.placesRecyclerView.layoutManager = layoutManager
        val adapter = PlaceListAdapter(places, itemClickListener)
        holder.placesRecyclerView.adapter = adapter

    }

    override fun getItemCount(): Int {
        return placesByDay.size
    }
}

class PlaceListAdapter(private val places: List<Place>,
                       private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName: TextView = itemView.findViewById(R.id.placeName)
        val placeImage: ImageView = itemView.findViewById(R.id.placeImage)
        val placeid:TextView=itemView.findViewById(R.id.placeid)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place_custom, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.placeName.text = place.name
        holder.placeid.text=place.placeId
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(place.placeId)
        }

        // Interface for the click listener


        // Load image using Glide library
        place.photos?.firstOrNull()?.let { photo ->
            val photoUrl = getPhotoUrl(photo.photoReference)
            Glide.with(holder.itemView)
                .load(photoUrl)
                .centerCrop() // Scale type for the image
                .into(holder.placeImage)
        }
    }

    override fun getItemCount(): Int {
        return places.size
    }

    private fun getPhotoUrl(photoReference: String): String {
        return "https://maps.googleapis.com/maps/api/place/photo" +
                "?maxwidth=400" + // Adjust size as per your requirement
                "&photoreference=$photoReference" +
                "&key=$API_KEY"
    }

}
