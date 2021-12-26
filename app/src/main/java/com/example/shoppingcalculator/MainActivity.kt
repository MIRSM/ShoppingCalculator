package com.example.shoppingcalculator

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var layoutManager : RecyclerView.LayoutManager? = null
    private var adapter : RecyclerView.Adapter<RecyclerProductAdapter.ViewHolder>? = null
    private val listOfProduct : ArrayList<Product> = ArrayList()

    private lateinit var resultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutManager = LinearLayoutManager(this)

        val recyclerView : RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager

        adapter = RecyclerProductAdapter(listOfProduct, this)
        // получить данные о покупках из БД
        // -> сформировать список продуктов
        //(adapter as RecyclerProductAdapter).listOfProduct =

        recyclerView.adapter = adapter

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 2) {
                // запись о продукте изменилась
                val data: Intent? = result.data
                val product = data!!.getSerializableExtra("result") as Product
                (adapter as RecyclerProductAdapter).editProduct(product)
            } else if (result.resultCode == Activity.RESULT_FIRST_USER) {
                // добавилась запись
                val data: Intent? = result.data
                val product = data!!.getSerializableExtra("result") as Product
                (adapter as RecyclerProductAdapter).addProduct(product)
            }
        }

        val addButton : FloatingActionButton = findViewById(R.id.floatingActionButton)
        addButton.setOnClickListener {
            //переход на страницу редактирования продукта
            val intent =  Intent(this, EditActivity::class.java)
            resultLauncher.launch(intent)
        }
    }
}