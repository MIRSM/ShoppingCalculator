package com.example.shoppingcalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerProductAdapter:RecyclerView.Adapter<RecyclerProductAdapter.ViewHolder>() {
    lateinit var listOfProduct: ArrayList<Product>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerProductAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.product_card_layout,parent,false)
        return  ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerProductAdapter.ViewHolder, position: Int) {
        holder.itemName.text = listOfProduct[position].name
        holder.itemVal.text = listOfProduct[position].totalVal.toString()
        holder.itemPrice.text = listOfProduct[position].price.toString()
        holder.itemImage.setImageBitmap(listOfProduct[position].image)
    }

    override fun getItemCount(): Int {
        return listOfProduct.count()
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var itemImage : ImageView
        var itemName : TextView
        var itemPrice : TextView
        var itemVal : TextView

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemName = itemView.findViewById(R.id.item_name)
            itemPrice = itemView.findViewById(R.id.item_price)
            itemVal = itemView.findViewById(R.id.item_val)
        }
    }
}