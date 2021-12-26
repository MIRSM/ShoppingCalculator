package com.example.shoppingcalculator

import android.net.Uri
import java.io.Serializable

class Product(var name: String = "", var count: Float = 0.0f, var price: Float = 0.0f, var imagePath: Uri?) : Serializable {
    val totalVal : Float
        get() {
            return  count * price
        }
    var tableId : Int? = null
}