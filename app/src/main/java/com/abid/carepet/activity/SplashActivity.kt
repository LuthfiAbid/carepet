package com.abid.carepet.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.abid.carepet.R
import com.abid.carepet.data.Pref

class SplashActivity : AppCompatActivity() {

    lateinit var pref: Pref
    private val SPLASH_TIME_OUT: Long = 2000 // 3 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        pref = Pref(this)

        if (pref.cekStatus()!!) {
            Handler().postDelayed({
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }, SPLASH_TIME_OUT)
        } else {

        }
    }
}
