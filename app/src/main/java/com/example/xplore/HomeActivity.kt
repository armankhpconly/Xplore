package com.example.xplore

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.xplore.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the initial fragment
        loadFragment(Home())

        // Handle navigation item clicks
        binding.bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment = Home()
        when (item.itemId) {
            R.id.home -> selectedFragment = Home()
            R.id.customalgo -> selectedFragment = Customiternary()
            R.id.community -> selectedFragment = CommunityFragment()
            R.id.profile -> selectedFragment = profile()
        }
        loadFragment(selectedFragment)
        true
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun onLogoutButtonClick(view: View) {
        FirebaseAuth.getInstance().signOut()

        // Redirect the user to the login activity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
