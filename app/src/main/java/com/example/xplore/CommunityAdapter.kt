package com.example.xplore

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xplore.databinding.ItemReviewBinding

class CommunityAdapter(private val reviews: List<AddReviewActivity.Review>) :
    RecyclerView.Adapter<CommunityAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount() = reviews.size

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: AddReviewActivity.Review) {
            Log.d("CommunityAdapter", "Binding review: ${review.placeName}, Sentiment Score: ${review.sentimentScore}, Magnitude: ${review.sentimentMagnitude}")

            binding.placeName.text = review.placeName
            binding.reviewText.text = review.reviewText
//            binding.sentimentText.text = "Sentiment Score: ${review.sentimentScore}, Magnitude: ${review.sentimentMagnitude}"
            binding.sentimentText.text = convertSentiment(review.sentimentScore)
        }
    }
}
private fun convertSentiment(sentimentScore:Float):String{
    return when{
        sentimentScore > 0.5 -> "AI Analysis : Good"
        sentimentScore < -0.5 -> "AI Analysis : Bad"
        else -> "AI Analysis : Neutral"

    }
}