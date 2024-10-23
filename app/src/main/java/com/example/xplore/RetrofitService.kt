package com.example.xplore

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Retrofit service interface
interface RetrofitService {
    @GET("place/details/json")
    fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String,
        @Query("key") apiKey: String
    ): Call<ApiPlaceDetailsResponse>


    @GET("directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") apiKey: String
    ): Call<DirectionsResponse>
    @GET("place/textsearch/json")
    fun getPlacesByCategory(
        @Query("query") query: String,
        @Query("location") location: String,
        @Query("key") apiKey: String
    ): Call<PlacesResponse>

    @GET("place/nearbysearch/json")
    fun getNearbyPlacesByCategory(
        @Query("keyword") keyword: String,
        @Query("location") location: String,
        @Query("radius") radius: Int = 5000,
        @Query("key") apiKey: String
        
    ): Call<PlacesResponse>

    data class SentimentRequest(val document: Document)
    data class Document(val type: String, val content: String)
    data class SentimentResponse(val documentSentiment: Sentiment)
    data class Sentiment(val score: Float, val magnitude: Float)

    interface GoogleNLPService {
        @POST("v1/documents:analyzeSentiment")
        fun analyzeSentiment(
            @Query("key") apiKey: String,
            @Body request: SentimentRequest
        ): Call<SentimentResponse>
    }
}
// Retrofit client object
object RetrofitClient {
    private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

    val retrofitService: RetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)
    }
}
