package edu.uw.ee523.promosnearsme

import android.os.Bundle
import android.widget.Button

class QrActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_qr)

        val but:Button = findViewById(R.id.ReturnButoon)
        but.setOnClickListener {
            finish()
        }
    }
}