package com.example.dowooz

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CourierDelivering : Fragment() {
    private lateinit var orderIdField : TextView
    private lateinit var timeLeft : TextView
    private lateinit var timeLeftMin : TextView
    private lateinit var callClient : Button
    private lateinit var clientNumber : TextView
    private lateinit var clientAddress : TextView
    private lateinit var naviButton : Button
    private lateinit var clientAdditionalInfo : TextView
    private lateinit var arrivedButton : Button
    private lateinit var orderPrice : TextView
    private lateinit var cashPayment : Button
    private lateinit var cardPayment : Button
    private lateinit var callRestaurant : Button
    private lateinit var endOrder : Button
    private lateinit var databaseReferenceOrders: DatabaseReference
    private lateinit var databaseReferencePrice: DatabaseReference
    private lateinit var secondLastOrderRandomName: String
    private lateinit var amountToAdd: String
    private var oldPrice:Float= 0F
    private var newPrice:Float= 0F
    private var orderCounter:Int=1
    private var destinationAddress:String=""
    private var clientPhoneNumber:String=""
    private val calendar = Calendar.getInstance()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var minutesLeft:Long = 0
    private lateinit var timeLeftMinText:TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.courier_delivering, container, false)
        orderIdField=view.findViewById(R.id.orderIdField)
        timeLeft=view.findViewById(R.id.timeNormModifier)
        timeLeftMin=view.findViewById(R.id.timeLeftModifier)
        timeLeftMinText =view.findViewById(R.id.timeLeft)
        callClient=view.findViewById(R.id.callClient)
        clientNumber=view.findViewById(R.id.clientInfoPhoneNumberModifier)
        clientAddress=view.findViewById(R.id.clientInfoAdressModifier)
        naviButton=view.findViewById(R.id.gpsButton)
        clientAdditionalInfo=view.findViewById(R.id.clientInfoAdditionalInfoModifier)
        arrivedButton=view.findViewById(R.id.arrivedButton)
        orderPrice=view.findViewById(R.id.priceToPay)
        cashPayment=view.findViewById(R.id.cashPayment)
        cardPayment=view.findViewById(R.id.cardPayment)
        callRestaurant=view.findViewById(R.id.completionProblemButton)
        endOrder=view.findViewById(R.id.orderCompleteButton)
        databaseReferenceOrders = FirebaseDatabase.getInstance("https://dowooz-default-rtdb.europe-west1.firebasedatabase.app/").getReference("orders")
        databaseReferencePrice = FirebaseDatabase.getInstance("https://dowooz-default-rtdb.europe-west1.firebasedatabase.app/").getReference("price")

        cashPayment.isEnabled=false;
        cardPayment.isEnabled=false;
        endOrder.isEnabled=false;
        updateTimeViews()

        mainHandler.post(object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                minutesLeft--
                if(minutesLeft<0)
                {
                    timeLeftMinText.text="Przekroczono czas o: "
                    timeLeftMin.text= kotlin.math.abs(minutesLeft).toString()+" min"
                }
                else
                {
                    timeLeftMin.text= "$minutesLeft min"
                }
                mainHandler.postDelayed(this, 1000*60)
            }
        })

        databaseReferenceOrders.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.childrenCount >= 1) {
                    val orders = dataSnapshot.children.toList()
                    orderCounter=orders.size
                    val secondLastOrder = orders[0].getValue(Order::class.java)
                    if (secondLastOrder != null) {
                        secondLastOrderRandomName = orders[0].key.orEmpty()
                        amountToAdd= secondLastOrder.price.toString()
                        orderIdField.text = "Zamówienie nr. ${secondLastOrder.id}"
                        clientNumber.text =secondLastOrder.phone.toString()
                        clientPhoneNumber=secondLastOrder.phone.toString()
                        clientAddress.text = secondLastOrder.address.toString()
                        destinationAddress=secondLastOrder.address.toString()
                        clientAdditionalInfo.text = "Uwagi: ${secondLastOrder.additionalInfo.toString()}"
                        orderPrice.text = secondLastOrder.price?.toString() ?: ""
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                showToast("Bład bazy danych")
            }
        })



        naviButton.setOnClickListener(){
            val mapsIntentUri = Uri.parse("geo:0,0?q=$destinationAddress")
            val mapIntent = Intent(Intent.ACTION_VIEW, mapsIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        callClient.setOnClickListener(){
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+48$clientPhoneNumber")
            startActivity(intent)
        }

        callRestaurant.setOnClickListener(){
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+48$clientPhoneNumber")
            startActivity(intent)
        }
        arrivedButton.setOnClickListener(){
            cashPayment.isEnabled=true;
            cardPayment.isEnabled=true;
        }

        cashPayment.setOnClickListener(){
            databaseReferencePrice.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val priceValue = dataSnapshot.getValue(String::class.java)
                    if (priceValue != null)
                    {
                        oldPrice = priceValue.toFloat()
                        newPrice = oldPrice + (amountToAdd.toFloat())
                        databaseReferencePrice.setValue(newPrice.toString())
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    showToast("error idk")
                }
            })
            orderPrice.text="Zapłacono"
            endOrder.isEnabled=true;
        }

        cardPayment.setOnClickListener(){
            showToast("Pobierz odpowiednią kwotę na terminalu")
            orderPrice.text="Zapłacono"
            endOrder.isEnabled=true;

        }



        endOrder.setOnClickListener(){
                databaseReferenceOrders.child(secondLastOrderRandomName).removeValue()
                showToast("Zakończono dostawę")
                orderCounter--

                if(orderCounter>1)
                {
                    val fragmentTransaction:FragmentTransaction=parentFragmentManager.beginTransaction()
                    fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.slide_out
                    )
                    fragmentTransaction.replace(R.id.mainContainer, CourierTaskChoice())
                    fragmentTransaction.commit()
                }
                else
                {
                    val fragmentTransaction:FragmentTransaction=parentFragmentManager.beginTransaction()
                    fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.slide_out
                    )
                    fragmentTransaction.replace(R.id.mainContainer, CourierNoTask())
                    fragmentTransaction.commit()
                }
            }
        return view
    }
    private fun updateTimeViews() {

        calendar.add(Calendar.MINUTE, 15)
        val futureTime = calendar.time


        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedTime = sdf.format(futureTime)

        timeLeft.text = formattedTime.toString()


        val currentTime = System.currentTimeMillis()
        val timeDifference = futureTime.time - currentTime
        minutesLeft = timeDifference / (1000 * 60) + 2

        timeLeftMin.text = "$minutesLeft min"
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}