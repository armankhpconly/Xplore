package com.example.xplore


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ContactUsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contact_us, container, false)

        val contactInfoText: TextView = view.findViewById(R.id.contactInfoText)
        contactInfoText.text = """
            Contact Information:

            Developer: Arman Khan
            Email: arman.khan@example.com
            Phone: +91 XXXXXXXXXX

            This app is a final year project for MScIT. 
            For any queries or feedback, please feel free to reach out.
        """.trimIndent()

        return view
    }
}