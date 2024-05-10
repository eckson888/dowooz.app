package com.example.dowooz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class CourierTaskChoice : Fragment() {
    private lateinit var acceptButton: Button
    private lateinit var denyButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.courier_new_task, container, false)

        acceptButton=view.findViewById(R.id.acceptTaskButton)
        denyButton=view.findViewById(R.id.denyQuestButton)

        acceptButton.setOnClickListener {
            val fragmentTransaction:FragmentTransaction=parentFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
            )
            fragmentTransaction.replace(R.id.mainContainer, CourierGoToRestaurant())
            fragmentTransaction.commit()
        }
        denyButton.setOnClickListener {
            val fragmentTransaction:FragmentTransaction=parentFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
            )
            fragmentTransaction.replace(R.id.mainContainer, CourierNoTask())
            fragmentTransaction.commit()
            showToast("Odrzucono zadanie, czekaj na następne")
        }
        return view
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}