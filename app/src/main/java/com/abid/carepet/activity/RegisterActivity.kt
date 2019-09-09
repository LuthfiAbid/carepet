package com.abid.carepet.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abid.carepet.R
import com.abid.carepet.data.Pref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    var value = 0.0
    lateinit var fAuth: FirebaseAuth
    lateinit var dbRef: DatabaseReference
    lateinit var helperPref: Pref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        helperPref = Pref(this)
        fAuth = FirebaseAuth.getInstance()

        tv_to_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btn_register.setOnClickListener {
            var name = et_namalengkap.text.toString()
            var address = et_alamat.text.toString()
            var phone = et_nomor_telepon.text.toString()
            var email = et_email_register.text.toString()
            var password = et_password_register.text.toString()
            if (name.isNotEmpty() || address.isNotEmpty() || phone.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty()
            ) {
                fAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            simpanToFirebase(name, address, phone, email, password)
                            Toast.makeText(this, "Register Berhasil!", Toast.LENGTH_SHORT).show()
                            onBackPressed()
                            startActivity(Intent(this, LoginActivity::class.java))
                        } else {
                            Toast.makeText(this, "Value must be 6 or more digit!", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "There's some empty input!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun simpanToFirebase(name: String, address: String, phone: String, email: String, password: String) {
        val uidUser = fAuth.currentUser?.uid
        val uid = helperPref.getUID()
        dbRef = FirebaseDatabase.getInstance().getReference("dataUser/$uidUser")
        dbRef.child("/id").setValue(uidUser)
        dbRef.child("/name").setValue(name)
        dbRef.child("/address").setValue(address)
        dbRef.child("/phone").setValue(phone)
        dbRef.child("/email").setValue(email)
        dbRef.child("/password").setValue(password)
    }
}
