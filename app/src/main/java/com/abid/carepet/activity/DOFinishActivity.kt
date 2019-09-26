package com.abid.carepet.activity

import android.os.Bundle
import android.util.Log
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import com.abid.carepet.R
import com.abid.carepet.data.Pref
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dofinish.*

class DOFinishActivity : AppCompatActivity(), RatingBar.OnRatingBarChangeListener {
    lateinit var pref: Pref
    lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dofinish)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "DETAIL ORDER"
        val orderid = intent.getStringExtra("id")

        dbRef = FirebaseDatabase.getInstance().getReference("order/$orderid")
        dbRef.orderByChild("status").equalTo("Finish").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })
        ratingBar.onRatingBarChangeListener = this

        var rating = ratingBar.rating

    }

    override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {

//            Toast.makeText(this,"Rating : $p1",Toast.LENGTH_SHORT).show()
    }
}
