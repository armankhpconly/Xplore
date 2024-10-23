package com.example.xplore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class AboutUsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about_us, container, false)

        val aboutUsText: TextView = view.findViewById(R.id.aboutUsText)
        aboutUsText.text = "Xplore is a final year project for MScIT developed by Arman Khan. " +
                "This app aims to help users discover and explore various locations and attractions. " +
                "Our mission is to make travel and exploration easier and more enjoyable for everyone."

        return view
    }
}