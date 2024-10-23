// PlaceAdapter.kt
package com.example.xplore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlaceAdapter(private val onPlaceClick: (Place) -> Unit) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    private var places: List<Place> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.bind(place)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    fun setPlaces(places: List<Place>) {
        this.places = places
        notifyDataSetChanged()
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.placeNameTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.placeDescriptionTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.placeImageView)

        fun bind(place: Place) {
            nameTextView.text = place.name
            descriptionTextView.text = place.vicinity
            Glide.with(itemView.context).load(place.photos?.firstOrNull()?.photoReference?.let { photoReference ->
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoReference&key=AIzaSyA2d9jCLU5jKFBp3-7o9O-MwvUwHzUAtos"
            }).into(imageView)

            itemView.setOnClickListener {
                onPlaceClick(place)
            }
        }
    }
}
