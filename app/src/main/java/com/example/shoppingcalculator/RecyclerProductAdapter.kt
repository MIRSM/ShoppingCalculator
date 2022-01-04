package com.example.shoppingcalculator

import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class RecyclerProductAdapter(private var listOfProduct: ArrayList<Product>, var parent : MainActivity) :RecyclerView.Adapter<RecyclerProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerProductAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.product_card_layout,parent,false)
        return  ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerProductAdapter.ViewHolder, position: Int) {
        holder.itemName.text = listOfProduct[position].name
        holder.itemVal.text = listOfProduct[position].totalVal.toString()
        holder.itemPrice.text = listOfProduct[position].price.toString()
        if((ContextCompat.checkSelfPermission(parent ,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
            listOfProduct[position].imagePath != null)
        {
            //parent.contentResolver.takePersistableUriPermission(listOfProduct[position].imagePath,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            //Picasso.get().load(listOfProduct[position].imagePath).placeholder(R.drawable.placeholder).error(R.drawable.ic_action_name).into(holder.itemImage)
            holder.itemImage.setImageBitmap(listOfProduct[position].imagePath?.let { Converters.toBitmap(it) })
        }
        else
            Picasso.get().load(R.drawable.placeholder).into(holder.itemImage)
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
                intent.putExtra(Constants.NAME_KEY,localProduct.name)
                intent.putExtra(Constants.COUNT_KEY,localProduct.count)
                intent.putExtra(Constants.PRICE_KEY,localProduct.price)
                intent.putExtra(Constants.IMAGEPATH_KEY,localProduct.imagePath)
                intent.putExtra(Constants.TABLEID_KEY,localProduct.tableId)
                if(localProduct.location != null){
                    intent.putExtra(Constants.LATITUDE_KEY,localProduct.location?.latitude)
                    intent.putExtra(Constants.LONGITUDE_KEY,localProduct.location?.longitude)
                }
                parent.resultLauncher.launch(intent,null)
            }
        }
    }

    fun updateList(newList: ArrayList<Product>){
        listOfProduct = newList
        notifyDataSetChanged()
    }
}