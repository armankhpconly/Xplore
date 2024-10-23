package com.example.xplore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xplore.databinding.FragmentCommunityBinding
import com.google.firebase.firestore.FirebaseFirestore

class CommunityFragment : Fragment() {
    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var communityAdapter: CommunityAdapter
    private val reviewsList = mutableListOf<AddReviewActivity.Review>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        setupRecyclerView()
        loadReviews()

        binding.addReviewButton.setOnClickListener {
            startActivity(Intent(activity, AddReviewActivity::class.java))
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        communityAdapter = CommunityAdapter(reviewsList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = communityAdapter
        }
    }

    private fun loadReviews() {
        firestore.collection("reviews").get().addOnSuccessListener { documents ->
            reviewsList.clear()
            for (document in documents) {
                val review = document.toObject(AddReviewActivity.Review::class.java)
                reviewsList.add(review)
            }
            communityAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
