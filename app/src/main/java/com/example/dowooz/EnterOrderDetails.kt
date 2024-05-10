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

class EnterOrderDetails : Fragment() {
    private lateinit var doneButton: Button
    private lateinit var abortButton: Button
    private lateinit var addressText1: TextView
    private lateinit var addressText2: TextView
    private lateinit var phoneText: TextView
    private lateinit var priceEdit: TextView
    private lateinit var additionalInfo: TextView
    //private lateinit var counter: Number
    private lateinit var databaseReference: DatabaseReference

    private var orderCounter: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_quest_details, container, false)

        doneButton=view.findViewById(R.id.acceptOrderDetails)
        abortButton=view.findViewById(R.id.cancelOrderDetails)
        addressText1=view.findViewById(R.id.addressInput)
        addressText2=view.findViewById(R.id.address2Input)
        phoneText=view.findViewById(R.id.phoneInput)
        priceEdit=view.findViewById(R.id.priceInput)
        additionalInfo=view.findViewById(R.id.additionalInfoInput)
        databaseReference =FirebaseDatabase.getInstance("https://dowooz-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("orders")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderCounter = snapshot.childrenCount.toInt()-1
            }
            override fun onCancelled(error: DatabaseError) {
                showToast("Błąd bazy danych")
            }
        })

        doneButton.setOnClickListener {
            val address = addressText1.text.toString()
            val address2 = addressText2.text.toString()
            val phone = phoneText.text.toString()
            val price = priceEdit.text.toString()
            val additionalInfo = additionalInfo.text.toString()

            if (address.isNotEmpty() && phone.isNotEmpty() && price.isNotEmpty()) {
                orderCounter++

                val orderId = orderCounter.toString()
                val fullAddress = address + if (address2.isNotEmpty()) "/$address2" else ""

                val newOrder = HashMap<String, Any>()
                newOrder["id"] = orderId
                newOrder["address"] = fullAddress
                newOrder["phone"] = phone
                newOrder["price"] = price
                newOrder["additionalInfo"] = additionalInfo

                databaseReference.push().setValue(newOrder)

                showToast("Zamówienie dodano pomyślnie")
            } else {
                showToast("Proszę wypełnić wymagane pola")
            }

            val fragmentTransaction:FragmentTransaction=parentFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
            )
            fragmentTransaction.replace(R.id.mainContainer, RestaurantPanel())
            fragmentTransaction.commit()
        }

        abortButton.setOnClickListener {
            val fragmentTransaction:FragmentTransaction=parentFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
            )
            fragmentTransaction.replace(R.id.mainContainer, RestaurantPanel())
            fragmentTransaction.commit()
            showToast("Anulowano")
        }
        return view
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}