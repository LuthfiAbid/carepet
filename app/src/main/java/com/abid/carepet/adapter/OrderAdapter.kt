package com.abid.carepet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abid.carepet.R
import com.abid.carepet.data.Pref
import com.abid.carepet.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {

    }

    lateinit var mCtx: Context
    lateinit var itemDestination: List<OrderModel>
    lateinit var pref: Pref
    lateinit var dbRef: DatabaseReference
    lateinit var fauth: FirebaseAuth

    constructor()
    constructor(mCtx: Context, list: List<OrderModel>) {
        this.mCtx = mCtx
        this.itemDestination = list
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OrderViewHolder {
        val view: View = LayoutInflater.from(p0.context)
            .inflate(R.layout.content_order, p0, false)
        val bukuViewHolder = OrderViewHolder(view)
        return bukuViewHolder
    }

    override fun getItemCount(): Int {
        return itemDestination.size
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llOrder: LinearLayout
        var tv_name: TextView
        var tv_startTime: TextView
        var tv_endTime: TextView
        var tv_status: TextView

        init {
            llOrder = itemView.findViewById(R.id.llOrder)
            tv_name = itemView.findViewById(R.id.nameProfileCarepet)
            tv_startTime = itemView.findViewById(R.id.startTimeText)
            tv_endTime = itemView.findViewById(R.id.endTimeText)
            tv_status = itemView.findViewById(R.id.tv_status_order)
        }
    }
}