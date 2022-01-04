package com.example.shoppingcalculator

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import kotlin.properties.Delegates

class Product(var name: String, var count: Float, var price: Float) : Serializable {
    val totalVal : Float
        get() {
            return  count * price
        }
    var tableId by Delegates.notNull<Int>()
    var imagePath : ByteArray? = null
    var location : LatLng? = null
}