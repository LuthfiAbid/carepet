package com.abid.carepet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abid.carepet.R
import com.abid.carepet.data.Pref
import com.abid.carepet.data.PrefSlider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var fAuth: FirebaseAuth
    private lateinit var pref: Pref
    private var preferenceHelper: PrefSlider? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        pref = Pref(this)
        preferenceHelper = PrefSlider(this)
        fAuth = FirebaseAuth.getInstance()


        if (!pref.cekStatus()!!) {
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        if (fAuth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
        }

        btn_login.setOnClickListener {
            var email = et_email_login.text.toString()
            var password = et_password_login.text.toString()

            if (email.isNotEmpty() || password.isNotEmpty()) {
                pref.setStatus(true)
                fAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("dataUser/${fAuth.currentUser?.uid}")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }
                                override fun onDataChange(p0: DataSnapshot) {
                                    val role = p0.child("status").value.toString()
                                    e("ROLEUSER", role)
                                    if (role == "user") {
                                        val user = fAuth.currentUser
                                        updateUI(user)
                                        Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                                        onBackPressed()
                                        finish()
                                    } else {
                                        Toast.makeText(this@LoginActivity, "Akun tidak terdaftar!", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            })
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Username atau Password salah!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(
                    this,
                    "Login Gagal!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btn_signup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            pref.saveUID(user.uid) //save uid sharedpreferences
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            Log.e("TAG_ERROR", "user tidak ada")
        }
    }
}
