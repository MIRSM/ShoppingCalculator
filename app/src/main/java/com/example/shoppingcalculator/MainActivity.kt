package com.example.shoppingcalculator

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.database.getBlobOrNull
import androidx.core.database.getDoubleOrNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var layoutManager : RecyclerView.LayoutManager? = null
    private var adapter : RecyclerView.Adapter<RecyclerProductAdapter.ViewHolder>? = null
    private lateinit var listOfProduct : ArrayList<Product>
    private lateinit var soundPool : SoundPool
    private var acceptSound : Int? = null
    lateinit var resultLauncher : ActivityResultLauncher<Intent>
    private lateinit var db : DatabaseHelper

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)

        layoutManager = LinearLayoutManager(this)

        storeProductsInList()

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }

        val recyclerView : RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        adapter = RecyclerProductAdapter(listOfProduct, this)
        recyclerView.adapter = adapter

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 2) {
                // запись о продукте изменилась
                val data: Intent? = result.data
                val name = data!!.getStringExtra(Constants.NAME_KEY)
                val count =data.getStringExtra(Constants.COUNT_KEY)
                val price = data.getStringExtra(Constants.PRICE_KEY)
                val imagePath = data.getByteArrayExtra(Constants.IMAGEPATH_KEY)
                val tableId = data.getIntExtra(Constants.TABLEID_KEY,-1)

                val product = Product(name!!,count!!.toFloat(),price!!.toFloat())
                if(data.hasExtra(Constants.LATITUDE_KEY)){
                    val latitude = data.getDoubleExtra(Constants.LATITUDE_KEY,0.0)
                    val longitude = data.getDoubleExtra(Constants.LONGITUDE_KEY,0.0)
                    product.location = LatLng(latitude,longitude)
                }
                product.imagePath = imagePath
                product.tableId = tableId

                db.updateData(product)

            } else if (result.resultCode == Activity.RESULT_FIRST_USER) {
                // добавилась запись
                val data: Intent? = result.data
                val name = data!!.getStringExtra(Constants.NAME_KEY)
                val count =data.getStringExtra(Constants.COUNT_KEY)
                val price = data.getStringExtra(Constants.PRICE_KEY)
                val imagePath = data.getByteArrayExtra(Constants.IMAGEPATH_KEY)

                val product = Product(name!!,count!!.toFloat(),price!!.toFloat())
                if(data.hasExtra(Constants.LATITUDE_KEY)){
                    val latitude = data.getDoubleExtra(Constants.LATITUDE_KEY,0.0)
                    val longitude = data.getDoubleExtra(Constants.LONGITUDE_KEY,0.0)
                    product.location = LatLng(latitude,longitude)
                }
                product.imagePath = imagePath

                db.addProduct(product)
            }
            storeProductsInList()
            (adapter as RecyclerProductAdapter).updateList(listOfProduct)
        }

        // инициализация аудио
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).build()
        acceptSound = soundPool.load(this,R.raw.accept,1)

        val addButton : FloatingActionButton = findViewById(R.id.floatingActionButton)
        addButton.setOnClickListener {
            // проиграть звук при нажатии кнопки
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
    private fun playSound(){
        acceptSound?.let { soundPool.play(it,1f,1f,0,0,1f) }
    }

    private fun storeProductsInList(){

        listOfProduct = ArrayList()
        val cursor = db.getAllProducts()
        if(cursor != null && cursor.count != 0){
            while (cursor.moveToNext()){
                val product = Product(cursor.getString(1), // имя
                    cursor.getFloat(2), // цена
                    cursor.getFloat(3)) // количество
                product.tableId = cursor.getInt(0)
                product.imagePath = cursor.getBlobOrNull(4)
                val latitude = cursor.getDoubleOrNull(5)
                val longitude = cursor.getDoubleOrNull(6)
                if(latitude != null && longitude != null)
                {
                    product.location = LatLng(cursor.getDouble(5),cursor.getDouble(6))
                }

                listOfProduct.add(product)
            }
        }
    }

    fun deleteProduct(product: Product){
        db.deleteData(product)
        storeProductsInList()
        (adapter as RecyclerProductAdapter).updateList(listOfProduct)
    }
}