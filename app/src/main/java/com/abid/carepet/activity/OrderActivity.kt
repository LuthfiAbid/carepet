package com.abid.carepet.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abid.carepet.R
import com.abid.carepet.adapter.OrderFinishAdapter
import com.abid.carepet.data.Pref
import com.abid.carepet.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dofinish.*
import java.util.*

class OrderActivity : AppCompatActivity() {

    private lateinit var fAuth: FirebaseAuth
    lateinit var pref: Pref
    private var orderFinishAdapter: OrderFinishAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var list: MutableList<OrderModel> = ArrayList()
    lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        fAuth = FirebaseAuth.getInstance()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "ORDER FINISH"
        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.rcOrder)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)

        dbRef = FirebaseDatabase.getInstance().getReference("order")
        dbRef.orderByChild("status").equalTo("Finish").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                list = ArrayList()
                for (dataSnapshot in data.children) {
                    val addDataAll = dataSnapshot.getValue(OrderModel::class.java)
                    addDataAll!!.key = dataSnapshot.key
                    if (addDataAll.userid == fAuth.currentUser?.uid) {
                        list.add(addDataAll)
                    }
                    Log.e("c", addDataAll.name)
                }
                orderFinishAdapter = OrderFinishAdapter(this@OrderActivity, list)
                recyclerView!!.adapter = orderFinishAdapter
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })
    }
}
