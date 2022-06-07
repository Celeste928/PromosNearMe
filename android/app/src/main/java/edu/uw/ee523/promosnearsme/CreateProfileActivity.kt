package edu.uw.ee523.promosnearsme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CreateProfileActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_create_profile)

        val doneButton: Button = findViewById(R.id.DoneButton)
        doneButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.DoneButton -> {
                startActivity(Intent(this, DoneProfileActivity::class.java))
                finish()
            }
        }
    }
}