package com.example.shoppingcalculator

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView


class RecyclerProductAdapter(private var listOfProduct: ArrayList<Product>, var parent : MainActivity) :RecyclerView.Adapter<RecyclerProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerProductAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.product_card_layout,parent,false)
        return  ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerProductAdapter.ViewHolder, position: Int) {
        holder.itemName.text = listOfProduct[position].name
        holder.itemVal.text = listOfProduct[position].totalVal.toString()
        holder.itemPrice.text = listOfProduct[position].price.toString()
        if(listOfProduct[position].imagePath?.toString() != "null")
            holder.itemImage.setImageURI(listOfProduct[position].imagePath)
        else
            holder.itemImage.setImageResource(R.drawable.placeholder)
        holder.itemCount.text = listOfProduct[position].count.toString()

        holder.localProduct = listOfProduct[position]
    }

    override fun getItemCount(): Int {
        return listOfProduct.count()
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var itemImage : ImageView
        var itemName : TextView
        var itemPrice : TextView
        var itemVal : TextView
        var itemCount : TextView
        lateinit var localProduct : Product

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemName = itemView.findViewById(R.id.item_name)
            itemPrice = itemView.findViewById(R.id.item_price)
            itemVal = itemView.findViewById(R.id.item_val)
            itemCount = itemView.findViewById(R.id.item_count)
            itemView.setOnClickListener{
                //переход на страницу редактирования продукта
                val intent =  Intent(parent, EditActivity::class.java)
                intent.putExtra("product",localProduct)
                startActivity(parent, intent,null)
            }
        }
    }

    fun addProduct(product: Product){
        listOfProduct.add(product)
        notifyDataSetChanged()
    }

    fun editProduct(product: Product){
        val index = listOfProduct.indexOf(product)
        listOfProduct[index] = product
        notifyDataSetChanged()
    }
}