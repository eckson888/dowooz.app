package com.example.dowooz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginFragm : Fragment() {

    private lateinit var loginButton: Button
    private lateinit var login: TextView
    private lateinit var pass: TextView

    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_page, container, false)

        databaseReference = FirebaseDatabase.getInstance("https://dowooz-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("users")

        loginButton=view.findViewById(R.id.loginButton)
        login=view.findViewById(R.id.loginLogin)
        pass=view.findViewById(R.id.loginPassword)

        loginButton.setOnClickListener {
            val enteredLogin=login.text.toString()
            val enteredPass=pass.text.toString()

            if(enteredLogin.isNotEmpty() && enteredPass.isNotEmpty())
            {
                authenticateAndSwitch(enteredLogin, enteredPass)
            }
            else
            {
                showToast("Wprowadź dane")
            }
        }
        return view
    }

    private fun authenticateAndSwitch(login: String, password: String)
    {
        databaseReference.orderByChild("login").equalTo(login)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot)
                {
                    if (dataSnapshot.exists())
                    {
                        for (userSnapshot in dataSnapshot.children)
                        {
                            val user = userSnapshot.getValue(User::class.java)
                            if (user != null && user.password == password)
                            {
                                when (user.type)
                                {
                                    "restaurant" -> switchToRestaurantFragment()
                                    "courier" -> switchToCourierFragment()
                                }
                                return
                            }
                        }
                        showToast("Nieprawidłowe hasło")
                    } else {
                        //nie istnieje taki ziutek
                        showToast("Nie znaleziono użytkownika")
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    showToast("Błąd bazy, spróbuj ponownie później")
                }
            })
    }

    private fun switchToRestaurantFragment() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.slide_in,
            R.anim.slide_out
        )
        fragmentTransaction.replace(R.id.mainContainer, RestaurantPanel(),"restaurantPanelTag")
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun switchToCourierFragment() {
        // sprawdzanie czy orders w bazie ma elementy
        val ordersReference = FirebaseDatabase.getInstance("https://dowooz-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("orders")
        ordersReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //sprawdzic wielkosc brancha orders
                val ordersSize = dataSnapshot.childrenCount.toInt()
                if (ordersSize > 1) { //placeholder pominac
                    // sa zamowienia
                    val fragmentTransaction = parentFragmentManager.beginTransaction()
                    fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.slide_out
                    )
                    fragmentTransaction.replace(R.id.mainContainer, CourierTaskChoice())
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                } else {
                    // nie ma dostepnych zamowien do wziecia
                    val fragmentTransaction = parentFragmentManager.beginTransaction()
                    fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.slide_out
                    )
                    fragmentTransaction.replace(R.id.mainContainer, CourierNoTask())
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                showToast("Błąd bazy danych")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}
