package com.abid.carepet.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abid.carepet.R
import com.abid.carepet.activity.DOFinishActivity
import com.abid.carepet.data.Pref
import com.abid.carepet.model.OrderModel

class OrderFinishAdapter : RecyclerView.Adapter<OrderFinishAdapter.OrderFinishViewHolder> {
    lateinit var mCtx: Context
    lateinit var itemOrder: List<OrderModel>
    lateinit var pref: Pref

    constructor()
    constructor(mCtx: Context, list: List<OrderModel>) {
        this.mCtx = mCtx
        this.itemOrder = list
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OrderFinishViewHolder {
        val view: View = LayoutInflater.from(p0.context)
            .inflate(R.layout.content_order_finish, p0, false)
        val bukuViewHolder = OrderFinishViewHolder(view)
        return bukuViewHolder
    }

    override fun getItemCount(): Int {
        return itemOrder.size
    }

    override fun onBindViewHolder(holder: OrderFinishViewHolder, position: Int) {
        val orderModel: OrderModel = itemOrder.get(position)
        holder.tv_name.text = orderModel.name.toString()
        holder.tv_startTime.text = orderModel.startTime.toString()
        holder.tv_endTime.text = orderModel.endTime.toString()
        holder.tv_status.text = orderModel.status.toString()
        holder.llOrder.setOnClickListener {
            val intent = Intent(mCtx, DOFinishActivity::class.java)
            intent.putExtra("id", orderModel.orderid)
            mCtx.startActivity(intent)
        }
    }

    inner class OrderFinishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llOrder: LinearLayout
        var tv_name: TextView
        var tv_startTime: TextView
        var tv_endTime: TextView
        var tv_status: TextView

        init {
            llOrder = itemView.findViewById(R.id.llOrderFinish)
            tv_name = itemView.findViewById(R.id.nameProfileFinish)
            tv_startTime = itemView.findViewById(R.id.startTimeFinish)
            tv_endTime = itemView.findViewById(R.id.endTimeFinish)
            tv_status = itemView.findViewById(R.id.tv_status_order)
        }
    }
}