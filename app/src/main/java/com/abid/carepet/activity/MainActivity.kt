package com.abid.carepet.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abid.carepet.R
import com.abid.carepet.data.Pref
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var pref: Pref
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pref = Pref(this)
        fAuth = FirebaseAuth.getInstance()

        btn_logout.setOnClickListener {
            pref.setStatus(false)
            fAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
