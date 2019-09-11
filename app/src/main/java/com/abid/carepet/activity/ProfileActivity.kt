package com.abid.carepet.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abid.carepet.R
import com.abid.carepet.data.Pref
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    lateinit var dbRef: DatabaseReference
    lateinit var pref: Pref
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        pref = Pref(this)
        fAuth = FirebaseAuth.getInstance()
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "PROFILE"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        btn_logout.setOnClickListener {
            fAuth.signOut()
            pref.setStatus(false)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btn_edit_profile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        FirebaseDatabase.getInstance().getReference("dataUser/${fAuth.uid}")
            .child("profile").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.value == null) {
                        Glide.with(this@ProfileActivity).load(R.drawable.user)
                            .centerCrop()
                            .error(R.drawable.ic_launcher_background)
                            .into(image_profile)
                    } else {
                        Glide.with(this@ProfileActivity).load(p0.value.toString())
                            .centerCrop()
                            .error(R.drawable.ic_launcher_background)
                            .into(image_profile)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })

        FirebaseDatabase.getInstance().getReference("dataUser/${fAuth.uid}")
            .child("name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    tv_name_profile.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        FirebaseDatabase.getInstance().getReference("dataUser/${fAuth.uid}")
            .child("email").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    tv_email_profile.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        FirebaseDatabase.getInstance().getReference("dataUser/${fAuth.uid}")
            .child("phone").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    tv_phone_profile.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }
}
