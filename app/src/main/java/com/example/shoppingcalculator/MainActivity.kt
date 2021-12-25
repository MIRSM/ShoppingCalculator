package com.example.shoppingcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var layoutManager : RecyclerView.LayoutManager? = null
    private var adapter : RecyclerView.Adapter<RecyclerProductAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutManager = LinearLayoutManager(this)

        val recyclerView : RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager

        adapter = RecyclerProductAdapter()
        // получить данные о покупках из БД
        // -> сформировать список продуктов
        //(adapter as RecyclerProductAdapter).listOfProduct =

        recyclerView.adapter = adapter

        val addButton : FloatingActionButton = findViewById(R.id.floatingActionButton)
        addButton.setOnClickListener {
            (adapter as RecyclerProductAdapter).listOfProduct.add(Product("",0f,0f,null))
        }


        //val imageId = intArrayOf()
    }
}