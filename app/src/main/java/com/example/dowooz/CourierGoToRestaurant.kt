package com.example.dowooz

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class CourierGoToRestaurant : Fragment() {
    private lateinit var naviButton: Button
    private lateinit var arrivedButton: Button
    private lateinit var readyButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.courier_coming, container, false)

        naviButton=view.findViewById(R.id.gpsButton)
        arrivedButton=view.findViewById(R.id.arrivedButton)
        readyButton=view.findViewById(R.id.orderReadyButton)
        readyButton.isEnabled = false

        naviButton.setOnClickListener {
            val destinationAddress = "Akademicka 9, Lublin"
            val mapsIntentUri = Uri.parse("google.navigation:q=$destinationAddress")
            val mapIntent = Intent(Intent.ACTION_VIEW, mapsIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)

        }

        arrivedButton.setOnClickListener {
            readyButton.isEnabled = true
        }
        
        readyButton.setOnClickListener {
            val fragmentTransaction:FragmentTransaction=parentFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
            )
            fragmentTransaction.replace(R.id.mainContainer, CourierDelivering())
            fragmentTransaction.commit()
        }
        return view
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}