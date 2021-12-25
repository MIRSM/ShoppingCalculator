package com.example.shoppingcalculator

import android.graphics.Bitmap

class Product(var name: String = "", var count: Float = 0.0f, var price: Float = 0.0f, public var image: Bitmap?) {
    public val totalVal : Float
        get() {
            return  count * price
        }
}