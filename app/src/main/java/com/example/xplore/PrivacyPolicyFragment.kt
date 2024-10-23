package com.example.xplore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class PrivacyPolicyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_privacy_policy, container, false)

        val privacyPolicyText: TextView = view.findViewById(R.id.privacyPolicyText)
        privacyPolicyText.text = """
            Privacy Policy for Xplore:

            1. Information Collection: We collect minimal personal information necessary for app functionality.
            2. Data Usage: Your data is used solely to improve app experience and is never sold to third parties.
            3. Data Protection: We implement industry-standard security measures to protect your information.
            4. User Rights: You have the right to access, modify, or delete your personal data at any time.

            For more information, please contact the developer, Arman Khan.
        """.trimIndent()

        return view
    }
}