package com.abid.carepet.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class OrderModel(
    var key: String? = "",
    var name: String? = "",
    var startTime: String? = "",
    var endTime: String? = "",
    var status: String? = "",
    var userid: String? = "",
    var orderid: String? = "",
    var rating: String? = ""
) : Serializable