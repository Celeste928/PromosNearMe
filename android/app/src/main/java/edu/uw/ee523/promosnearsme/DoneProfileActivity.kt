package edu.uw.ee523.promosnearsme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DoneProfileActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_done_profile)

        val doneButton: Button = findViewById(R.id.GoHomeButton)
        doneButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}