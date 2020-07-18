package com.priyansh.foodapp.activity.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.priyansh.foodapp.R

class Splashscreen : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this,
                LoginActivity::class.java)
            startActivity(intent)
            finish()
        },2500)


    }
}
