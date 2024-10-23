package com.example.xplore

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ApiPlaceDetailsResponse(
    @SerializedName("result") val result: ApiPlaceDetailsResult?
)

data class ApiPlaceDetailsResult(
    @SerializedName("name") val name: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("photos") val photos: List<ApiPhoto>?,
    @SerializedName("geometry") val geometry: ApiGeometry?
)

data class ApiPhoto(
    @SerializedName("photo_reference") val photoReference: String?
)

data class ApiGeometry(
    @SerializedName("location") val location: ApiLocation?
)

data class ApiLocation(
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lng") val lng: Double?
)

data class DirectionsResponse(
    @SerializedName("routes") val routes: List<Route>?
)

data class Route(
    @SerializedName("legs") val legs: List<Leg>?
)

data class Leg(
    @SerializedName("duration") val duration: Duration?,
    @SerializedName("distance") val distance: Distance?,
    @SerializedName("steps") val steps: List<Step>?
)

data class Step(
    @SerializedName("html_instructions") val htmlInstructions: String,
    @SerializedName("duration") val duration: Duration,
    @SerializedName("distance") val distance: Distance,
    @SerializedName("polyline") val polyline: Polyline // Added the polyline property
)

data class Duration(
    @SerializedName("text") val text: String
)

data class Distance(
    @SerializedName("text") val text: String
)

data class Polyline(
    @SerializedName("points") val points: String // Use points here
)
data class PlacesResponse(
    @SerializedName("results") val results: List<Place>
)

data class Place(
    @SerializedName("place_id") val placeId: String,
    @SerializedName("name") val name: String,
    @SerializedName("vicinity") val vicinity: String,
    @SerializedName("photos") val photos: List<Photo>?
)

data class Photo(
    @SerializedName("photo_reference") val photoReference: String
)
data class DayWithPlaces(
    @SerializedName("day") val day: Date,
    @SerializedName("places") val places: List<Place>
)