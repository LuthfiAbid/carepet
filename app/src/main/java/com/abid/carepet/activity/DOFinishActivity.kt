package com.abid.carepet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.abid.carepet.R
import com.abid.carepet.data.Pref
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dofinish.*
import kotlinx.android.synthetic.main.rating_dialog.view.*

class DOFinishActivity : AppCompatActivity() {
    lateinit var pref: Pref
    lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dofinish)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "DETAIL ORDER"
        val orderid = intent.getStringExtra("id")

        btn_report.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            intent.putExtra("id", orderid)
            startActivity(intent)
        }

        btn_rating.setOnClickListener {
            showDialog()
        }

        dbRef = FirebaseDatabase.getInstance().getReference("order/$orderid/rating")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (!p0.exists()) {
                    rated.visibility = View.GONE
                    ratingBar.visibility = View.GONE
                    textRating.visibility = View.GONE
                    btn_rating.visibility = View.VISIBLE
                } else {
                    rated.visibility = View.VISIBLE
                    ratingBar.visibility = View.VISIBLE
                    textRating.visibility = View.VISIBLE
                    ratingBar.rating = p0.value.toString().toFloat()
                    btn_rating.visibility = View.GONE
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })
        dbRef = FirebaseDatabase.getInstance().getReference("order/$orderid")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                orderIdText.text = p0.child("orderid").value.toString()
                startTimeDetail.text = p0.child("startTime").value.toString()
                endTimeDetail.text = p0.child("endTime").value.toString()
                if (p0.child("rating").exists()) {
                    var rating = p0.child("rating").value.toString().toFloat()
                    when (rating) {
                        in 0..2 -> textRating.text = "Bad"
                        in 3..4 -> textRating.text = "Good"
                        else -> textRating.text = "Excellent!"
                    }
                } else {

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this@DOFinishActivity)
        val dialogView = layoutInflater.inflate(R.layout.rating_dialog, null)

        builder.setView(dialogView)
            .setPositiveButton("OK") { dialogInterface, i ->
                var rating = dialogView.dialogRb.rating.toString()
                var note = dialogView.dialogEt.text.toString()
                addToFirebase(rating, note)
            }
            .setNegativeButton("DISMISS") { dialogInterface, i ->

            }
            .show()
    }

    private fun addToFirebase(rating: String, note: String) {
        val orderid = intent.getStringExtra("id")
        val dbRef = FirebaseDatabase.getInstance().getReference("order/$orderid")
        dbRef.child("rating").setValue(rating)
        dbRef.child("ratingNote").setValue(note)
//        startActivity(intent)
//        finish()
    }
}


