package com.example.shoppingcalculator

import android.net.Uri
import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

class Product(var name: String? = "", var count: Float = 0.0f, var price: Float = 0.0f) : Serializable {
    val totalVal : Float
        get() {
            return  count * price
        }
    var tableId : Int? = null
    var imagePath : Uri? = null
    var location : GeoPoint? = null
}