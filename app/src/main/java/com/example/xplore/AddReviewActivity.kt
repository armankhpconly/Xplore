package com.example.xplore

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.xplore.databinding.ActivityAddReviewBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddReviewActivity : AppCompatActivity() {
    data class Review(val placeName: String = "", val reviewText: String = "", val sentimentScore: Float = 0f, val sentimentMagnitude: Float = 0f)

    private lateinit var binding: ActivityAddReviewBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var nlpService: RetrofitService.GoogleNLPService
    private val apiKey = "AIzaSyAhl5BCuPNuPxM5Tye_NODGPj0sIghdyu8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirebaseFirestore.getInstance()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://language.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        nlpService = retrofit.create(RetrofitService.GoogleNLPService::class.java)

        binding.postButton.setOnClickListener {
            val placeName = binding.placeNameEditText.text.toString().trim()
            val reviewText = binding.reviewEditText.text.toString().trim()

            if (placeName.isNotEmpty() && reviewText.isNotEmpty()) {
                analyzeSentimentAndPostReview(placeName, reviewText)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun analyzeSentimentAndPostReview(placeName: String, reviewText: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val document = RetrofitService.Document("PLAIN_TEXT", reviewText)
                val request = RetrofitService.SentimentRequest(document)
                val response = nlpService.analyzeSentiment(apiKey, request).execute()
                if (response.isSuccessful) {
                    val sentiment = response.body()?.documentSentiment
                    if (sentiment != null) {
                        val review = Review(placeName, reviewText, sentiment.score, sentiment.magnitude)
                        firestore.collection("reviews").add(review)
                            .addOnSuccessListener {
                                runOnUiThread {
                                    Toast.makeText(this@AddReviewActivity, "Review added with sentiment score: ${sentiment.score}", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                            .addOnFailureListener { e ->
                                runOnUiThread {
                                    Toast.makeText(this@AddReviewActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@AddReviewActivity, "Error analyzing sentiment", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@AddReviewActivity, "Error analyzing sentiment: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
