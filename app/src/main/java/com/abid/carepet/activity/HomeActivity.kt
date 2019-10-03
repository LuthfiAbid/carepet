package com.abid.carepet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abid.carepet.R
import com.abid.carepet.adapter.OrderAdapter
import com.abid.carepet.data.Pref
import com.abid.carepet.model.OrderModel
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.nav_header_home.*
import java.util.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var fAuth: FirebaseAuth
    lateinit var pref: Pref
    private var orderAdapter: OrderAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var list: MutableList<OrderModel> = ArrayList()
    lateinit var dbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        fAuth = FirebaseAuth.getInstance()
        pref = Pref(this)
        var linearLayoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.recyclerViewOrder)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.setHasFixedSize(true)

        dbRef = FirebaseDatabase.getInstance().getReference("order/")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                list = ArrayList()
                for (dataSnapshot in data.children) {
                    val addDataAll = dataSnapshot.getValue(OrderModel::class.java)
                    addDataAll!!.key = dataSnapshot.key
                    if (dataSnapshot.child("status").value == "Finish" || dataSnapshot.child("status").value == "Rejected") {

                    } else {
                        if (addDataAll.userid == fAuth.currentUser?.uid) {
                            list.add(addDataAll)
                            val count = data.childrenCount.toString()
                            if (count > "0") {
                                fab.hide()
                            } else if (count < "0") {
                                fab.show()
                            }
                            e("COUNTCHILD", count.toString())
                        }
                    }
                    e("c", addDataAll.name)
                }
                orderAdapter = OrderAdapter(this@HomeActivity, list)
                recyclerView!!.adapter = orderAdapter
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e(
                    "TAG_ERROR", p0.message
                )
            }
        })
        FirebaseDatabase.getInstance().getReference("dataUser/${fAuth.uid}")
            .child("name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    textViewUser.text = p0.value.toString()
                }

                override fun onCancelled(p0: DatabaseError) {
                }

            })

        FirebaseDatabase.getInstance().getReference("dataUser/${fAuth.uid}")
            .child("profile").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.value == null) {
                        Glide.with(this@HomeActivity).load(R.drawable.user)
                            .centerCrop()
                            .error(R.drawable.ic_launcher_background)
                            .into(imageViewHome)
                    } else {
                        Glide.with(this@HomeActivity).load(p0.value.toString())
                            .centerCrop()
                            .error(R.drawable.ic_launcher_background)
                            .into(imageViewHome)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            startActivity(Intent(this, CarepetActivity::class.java))
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this, HomeActivity::class.java))
            }
            R.id.nav_order -> {
                startActivity(Intent(this, OrderActivity::class.java))
            }
            R.id.nav_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.nav_logout -> {
                pref.setStatus(false)
                fAuth.signOut()
                startActivity(
                    Intent(
                        this, LoginActivity::class.java
                    )
                )
                finish()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
