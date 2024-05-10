package com.example.dowooz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState==null)
        {
            val fragmentTransaction:FragmentTransaction=supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_out
            )
            fragmentTransaction.replace(R.id.mainContainer, LoginFragm())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

    }

}