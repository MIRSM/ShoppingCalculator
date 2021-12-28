package com.example.shoppingcalculator

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
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
    private lateinit var soundPool : SoundPool
    private var acceptSound : Int? = null
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
                //val product = data!!.getSerializableExtra("result") as Product
                val name = data!!.getStringExtra("name")
                val count =data.getStringExtra("count")
                val price = data.getStringExtra("price")
                val imagePath = data.getStringExtra("imageUri")
                val tableId = data.getIntExtra("tableId",-1)
                val product = Product(name,count!!.toFloat(),price!!.toFloat())
                product.imagePath = Uri.parse(imagePath)
                product.tableId = tableId

                (adapter as RecyclerProductAdapter).editProduct(product)
            } else if (result.resultCode == Activity.RESULT_FIRST_USER) {
                // добавилась запись
                val data: Intent? = result.data
                val name = data!!.getStringExtra("name")
                val count =data.getStringExtra("count")
                val price = data.getStringExtra("price")
                val imagePath = data.getStringExtra("imageUri")
                val product = Product(name,count!!.toFloat(),price!!.toFloat())
                product.imagePath = Uri.parse(imagePath)

                //val product = data!!.getSerializableExtra("result") as Product
                (adapter as RecyclerProductAdapter).addProduct(product)
            }
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).build()
        acceptSound = soundPool.load(this,R.raw.accept,1)

        val addButton : FloatingActionButton = findViewById(R.id.floatingActionButton)
        addButton.setOnClickListener {
            playSound()
            //переход на страницу редактирования продукта
            val intent =  Intent(this, EditActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
    fun playSound(){
        acceptSound?.let { soundPool.play(it,1f,1f,0,0,1f) }
    }
}