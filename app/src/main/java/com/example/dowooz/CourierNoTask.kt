package com.example.dowooz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CourierNoTask : Fragment() {

    private lateinit var logoutButton: Button
    private lateinit var sumAmount:TextView
    private var amountToCollectFloat: Float = 0.0f
    private lateinit var databaseReferencePrice:DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.courier_no_task, container, false)
        logoutButton=view.findViewById(R.id.logoutButton)
        sumAmount=view.findViewById(R.id.moneyToCountModifier)
        databaseReferencePrice = FirebaseDatabase.getInstance("https://dowooz-default-rtdb.europe-west1.firebasedatabase.app/").getReference("price")

        databaseReferencePrice.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val priceValue = dataSnapshot.getValue(String::class.java)
                if (priceValue != "0")
                {
                    sumAmount.text=priceValue
                }
                else
                {
                    sumAmount.text="0"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                showToast("error idk")
            }
        })
        logoutButton.setOnClickListener {

            val fragmentTransaction:FragmentTransaction=parentFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
            )
            fragmentTransaction.replace(R.id.mainContainer, LoginFragm())
            fragmentTransaction.commit()
            showToast("Wylogowano")
        }
        return view
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}
