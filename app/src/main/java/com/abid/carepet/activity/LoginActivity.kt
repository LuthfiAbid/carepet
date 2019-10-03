package com.abid.carepet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log.e
import android.view.View
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

        if (pref.cekStatus()!!) {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        } else {

        }

        btn_login.setOnClickListener {
            var email = et_email_login.text.toString()
            var password = et_password_login.text.toString()

            if (email.isNotEmpty() || password.isNotEmpty()) {
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
                                        pref.setStatus(true)
                                        btn_login.visibility = View.GONE
                                        progresbar_login.visibility = View.VISIBLE
                                        val user = fAuth.currentUser
                                        updateUI(user)
                                        Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                                        finish()

                                    } else {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Username atau Password salah!",
                                            Toast.LENGTH_SHORT
                                        )
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
                btn_login.visibility = View.VISIBLE
                progresbar_login.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Harap isi kolom yang kosong!",
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
            finish()
            pref.saveUID(user.uid) //save uid sharedpreferences
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            e("TAG_ERROR", "user tidak ada")
        }
    }
}
