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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RestaurantPanel : Fragment() {
    private lateinit var logoutButton: Button
    private lateinit var newOrderButton: Button
    private var amountToCollectFloat: Float = 0.0f
    private lateinit var amountToCollect: TextView
    private lateinit var databaseReferencePrice: DatabaseReference
    private lateinit var databaseReferenceOrders: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrdersAdapter
    private val ordersList = mutableListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.restaurant_add_quest, container, false)
        logoutButton=view.findViewById(R.id.logoutButton)
        newOrderButton=view.findViewById(R.id.callCourier)
        amountToCollect=view.findViewById(R.id.moneyToCollectModifier)
        databaseReferencePrice = FirebaseDatabase.getInstance("https://dowooz-default-rtdb.europe-west1.firebasedatabase.app/").getReference("price")
        databaseReferenceOrders = FirebaseDatabase.getInstance("https://dowooz-default-rtdb.europe-west1.firebasedatabase.app/").getReference("orders")
        recyclerView = view.findViewById(R.id.recyclerView)
        orderAdapter = OrdersAdapter(ordersList) { orderId ->
            deleteOrder(orderId)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = orderAdapter

        databaseReferenceOrders.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ordersList.clear()
                for (orderSnapshot in dataSnapshot.children) {
                    if(orderSnapshot.key!="placeholder")
                    {
                        val order = orderSnapshot.getValue(Order::class.java)
                        if (order != null) {
                            ordersList.add(order)
                        }
                    }
                }
                orderAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showToast("Błąd bazy danych")
            }
        })

        databaseReferencePrice.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val priceValue = dataSnapshot.getValue(String::class.java)
                if (priceValue != "0")
                {
                    amountToCollect.text=priceValue
                }
                else
                {
                    amountToCollect.text="0"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                showToast("error idk")
            }
        })

        newOrderButton.setOnClickListener {
            val fragmentTransaction:FragmentTransaction=parentFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
            )
            fragmentTransaction.replace(R.id.mainContainer, EnterOrderDetails())
            fragmentTransaction.commit()
        }

        logoutButton.setOnClickListener {
                    //dodac ifa ze jesli sa zamowienia to nie da sie wylogowac - do prezentacji w szkole nie dodawac
                    databaseReferencePrice.setValue("0")
                    val fragmentTransaction:FragmentTransaction=parentFragmentManager.beginTransaction()
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out)
                    fragmentTransaction.replace(R.id.mainContainer, LoginFragm())
                    fragmentTransaction.commit()
        }
        return view
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun deleteOrder(orderId: String) {
        if (orderId.isNotBlank()) {
            FirebaseDatabase.getInstance("https://dowooz-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("orders")
                .orderByChild("id")
                .equalTo(orderId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (orderSnapshot in dataSnapshot.children) {
                            orderSnapshot.ref.removeValue()
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        showToast("Błąd bazy danych")
                    }
                })
        }
    }
}