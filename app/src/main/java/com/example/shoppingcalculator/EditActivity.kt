package com.example.shoppingcalculator

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText


class EditActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var editName : TextInputEditText
    private lateinit var editPrice : TextInputEditText
    private lateinit var editCount : TextInputEditText

    private lateinit var resultLauncher : ActivityResultLauncher<Intent>

    private var oldProductName : String? = null
    private var oldProductPrice : Float? = null
    private var oldProductCount : Float? = null
    private var oldProductImage : Uri? = null
    private var newImageUri : Uri? = null
    private var product : Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            product = intent.getSerializableExtra("product") as Product
        }catch (ex : Exception){
            // product не был передан
        }

        val actionBar = supportActionBar
        if(actionBar != null){
            if(product != null)
                actionBar.title = "Редактирование продукта"
            else
                actionBar.title = "Создание продукта"
        }

        setContentView(R.layout.activity_edit)

        imageView = findViewById(R.id.edit_image)
        imageView.setOnClickListener{
            pickImageGallery()
        }

        editName = findViewById(R.id.edit_name)
        editPrice = findViewById(R.id.edit_price)
        editCount = findViewById(R.id.edit_count)

        if(product != null){
            oldProductName = product?.name
            oldProductPrice = product?.price
            oldProductCount = product?.count
            oldProductImage = product?.imagePath

            editName.setText(oldProductName)
            editPrice.setText(oldProductPrice.toString())
            editCount.setText(oldProductCount.toString())
            if(oldProductImage != null)
            {
                imageView.setImageURI(oldProductImage)
            }
        }

         resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data: Intent? = result.data
                newImageUri = data?.data
                imageView.setImageURI(newImageUri)
            }
        }
    }

    override fun onBackPressed() {
        if(product == null &&
            (editName.text.toString() == "" ||
                    editPrice.text.toString() == "" ||
                    editCount.text.toString() == ""))
        {
            setResult(RESULT_CANCELED)
        }else if(product != null &&
            oldProductName == editName.text.toString() &&
            oldProductPrice == editPrice.text.toString().toFloat() &&
            oldProductCount == editCount.text.toString().toFloat() &&
            oldProductImage == newImageUri)
        {
            setResult(RESULT_CANCELED)
        }else if(product == null){
            product = Product(editName.text.toString(),
                editCount.text.toString().toFloat(),
                editPrice.text.toString().toFloat(),
                newImageUri
            )

            val resultIntent = Intent()
            resultIntent.putExtra("result",product)

            setResult(RESULT_FIRST_USER,resultIntent)
        }else{
            product?.name = editName.text.toString()
            product?.price = editPrice.text.toString().toFloat()
            product?.count = editCount.text.toString().toFloat()
            product?.imagePath = newImageUri

            val resultIntent = Intent()
            resultIntent.putExtra("result",product)

            setResult(2,resultIntent)
        }
        finish()
    }

    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }
}