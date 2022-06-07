package edu.uw.ee523.promosnearsme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button


class OrderActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        actionBar?.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_order)

        val callButton: Button = findViewById(R.id.CallButton)
        callButton.setOnClickListener(this)

        val navigateButton: Button = findViewById(R.id.NavigateButton)
        navigateButton.setOnClickListener(this)

        val promoButton: Button = findViewById(R.id.ShowPromoButton)
        promoButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.CallButton -> {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "2068888888"))
                startActivity(intent)
            }
            R.id.NavigateButton -> {
                Log.i("TAG", "Hello")
                // Create a Uri from an intent string. Use the result to create an Intent.
                val gmmIntentUri = Uri.parse("google.navigation:q=4024 S Willow st&mode=w")

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
// Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps")

// Attempt to start an activity that can handle the Intent
                startActivity(mapIntent)
            }
            R.id.ShowPromoButton -> {
                val intent = Intent(this, QrActivity::class.java)
                startActivity(intent)
            }
        }
    }

}