package edu.uw.ee523.promosnearsme

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button

class MainActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_main)

        val createButton: Button = findViewById(R.id.CreateProfileButton)
        createButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.CreateProfileButton -> {
                startActivity(Intent(this, CreateProfileActivity::class.java))
                finish()
            }
        }
    }
}