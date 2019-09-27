package com.abid.carepet.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.abid.carepet.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dofinish.btn_report
import kotlinx.android.synthetic.main.activity_dofinish.toolbar
import kotlinx.android.synthetic.main.activity_report.*

class ReportActivity : AppCompatActivity() {
    lateinit var dbRef: DatabaseReference
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
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
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
        val dbRef = FirebaseDatabase.getInstance().getReference("reportOrder")
        dbRef.child("report").setValue(report)
        dbRef.child("orderid").setValue(orderid)
        dbRef.push()
        startActivity(intent)
        finish()
    }
}
