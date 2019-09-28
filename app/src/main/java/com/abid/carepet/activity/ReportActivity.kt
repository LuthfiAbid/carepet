package com.abid.carepet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log.e
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abid.carepet.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dofinish.btn_report
import kotlinx.android.synthetic.main.activity_dofinish.toolbar
import kotlinx.android.synthetic.main.activity_report.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ReportActivity : AppCompatActivity() {
    lateinit var dbRef: DatabaseReference
    var iduser = ""
    var user = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "HELP CENTER"
        val orderid = intent.getStringExtra("id")
        dbRef = FirebaseDatabase.getInstance().getReference("order/$orderid")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                current_time.text = p0.child("time").value.toString()
                iduser = p0.child("userid").value.toString()
                dbRef = FirebaseDatabase.getInstance().getReference("dataUser/$iduser")
                dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        user = p0.child("name").value.toString()
                        e("namaUserCok", user)
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })

            }

            override fun onCancelled(p0: DatabaseError) {
                e(
                    "TAG_ERROR", p0.message
                )
            }
        })
        btn_report.setOnClickListener {
            var report = et_report.text.toString()
            addToFireBase(report)
        }
    }

    private fun addToFireBase(report: String) {
        val orderid = intent.getStringExtra("id")
        val uid = UUID.randomUUID().toString()
        val current = LocalDateTime.now()
        val nama = user
        val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH.mm")
        val formatted = current.format(formatter)
        val dbRef = FirebaseDatabase.getInstance().getReference("reportOrder/$uid")
        dbRef.child("id").setValue(uid)
        dbRef.child("time").setValue(formatted)
        dbRef.child("report").setValue(report)
        dbRef.child("orderid").setValue(orderid)
        dbRef.child("username").setValue(nama)
        dbRef.push()
        Toast.makeText(this, "Terima kasih atas kritik dan masukan dari anda :)", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, OrderActivity::class.java))
        finish()
    }
}
