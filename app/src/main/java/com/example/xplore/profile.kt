package com.example.xplore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class profile : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        val aboutUsButton: Button = view.findViewById(R.id.about_us_button)
        aboutUsButton.setOnClickListener {
            navigateToFragment(AboutUsFragment())
        }

        val privacyPolicyButton: Button = view.findViewById(R.id.privacy_policy_button)
        privacyPolicyButton.setOnClickListener {
            navigateToFragment(PrivacyPolicyFragment())
        }
        val logoutButton: Button = view.findViewById(R.id.logout_button)

        logoutButton.setOnClickListener {
            // Clear user session data
            firebaseAuth.signOut()


            // Redirect to MainActivity
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)

            // Finish current activity and all activities in the stack
            activity?.finishAffinity()
        }




        return view
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
