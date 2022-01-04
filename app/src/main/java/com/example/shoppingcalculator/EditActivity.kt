package com.example.shoppingcalculator

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import java.io.File

class EditActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var imageView: ImageView
    private lateinit var editName : TextInputEditText
    private lateinit var editPrice : TextInputEditText
    private lateinit var editCount : TextInputEditText

    private lateinit var resultLauncher : ActivityResultLauncher<Intent>

    private lateinit var mMapView: MapView
    private lateinit var mFusedLocationClient : FusedLocationProviderClient

    private var oldProductName : String? = null
    private var oldProductPrice : Float? = null
    private var oldProductCount : Float? = null
    private var oldProductImage : String? = null
    //private var oldProductImageUri : Uri? = null
    private var oldProductTableId : Int? = null
    private var oldProductLocation : LatLng? = null

    private var newProductLocation : LatLng? = null
    private var newImageUri : Uri? = null
    private var oldBitmap : Bitmap? = null
    private var newBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            if(intent.hasExtra(Constants.NAME_KEY)){
                oldProductName = intent.getStringExtra(Constants.NAME_KEY)
                oldProductCount =intent.getFloatExtra(Constants.COUNT_KEY,0f)
                oldProductPrice = intent.getFloatExtra(Constants.PRICE_KEY,0f)
                oldProductTableId = intent.getIntExtra(Constants.TABLEID_KEY,-1)
                oldProductLocation = LatLng(intent.getDoubleExtra(Constants.LATITUDE_KEY, 0.0),
                    intent.getDoubleExtra(Constants.LONGITUDE_KEY,0.0))
                oldBitmap = intent.getByteArrayExtra(Constants.IMAGEPATH_KEY)?.let {
                    Converters.toBitmap(it) }
                //oldProductImageUri = Uri.parse(oldProductImage)
            }
        }catch (ex : Exception){
            // product не был передан
        }

        val actionBar = supportActionBar
        if(actionBar != null){
            if(oldProductName != null)
                actionBar.title = "Редактирование продукта"
            else
                actionBar.title = "Создание продукта"
        }

        setContentView(R.layout.activity_edit)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastKnownLocation()

        mMapView = findViewById(R.id.edit_map)
        initGoogleMap(savedInstanceState)

        imageView = findViewById(R.id.edit_image)
        imageView.setOnClickListener{
            pickImageGallery()
        }

        editName = findViewById(R.id.edit_name)
        editPrice = findViewById(R.id.edit_price)
        editCount = findViewById(R.id.edit_count)

        if(oldProductName != null){

            editName.setText(oldProductName)
            editPrice.setText(oldProductPrice.toString())
            editCount.setText(oldProductCount.toString())
            if(oldBitmap != null)
            {
                //parent.contentResolver.takePersistableUriPermission(oldProductImageUri!!,Intent.FLAG_GRANT_READ_URI_PERMISSION)
                //Picasso.get().load(oldBitmap).placeholder(R.drawable.placeholder).error(R.drawable.ic_action_name).into(imageView)
                imageView.setImageBitmap(oldBitmap)
                newBitmap = oldBitmap
            }
        }else{
            Picasso.get().load(R.drawable.placeholder).into(imageView)
        }

         resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if(result.resultCode == RESULT_OK){
                val data: Intent? = result.data
                newImageUri = data?.data
                //Picasso.get().load(newImageUri).placeholder(R.drawable.placeholder).into(imageView)
                imageView.setImageURI(newImageUri)
                newBitmap = imageView.drawable.toBitmap()
                //
            }
        }
    }

    private fun getLastKnownLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient.lastLocation.addOnCompleteListener{
            if(oldProductLocation == null && it.isSuccessful){
                val location = it.result
                oldProductLocation = LatLng(location.latitude,location.longitude)
                newProductLocation = oldProductLocation
            }
        }
    }

    private fun initGoogleMap(savedInstanceState: Bundle?){
        var mapViewBundle : Bundle? = null
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(Constants.MAPVIEW_BUNDLE_KEY)
        }
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(Constants.MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(Constants.MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mMapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onMapReady(map: GoogleMap) {
        map.setOnMapClickListener {
                map.clear()
                map.animateCamera(CameraUpdateFactory.newLatLng(it))
                newProductLocation = LatLng(it.latitude,it.longitude)
                map.addMarker(MarkerOptions().position(newProductLocation!!))
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }
        map.isMyLocationEnabled = true
        if(oldProductLocation != null)
            map.addMarker(MarkerOptions().position(oldProductLocation!!))
    }

    override fun onBackPressed() {
        if(oldProductName == null &&
            (editName.text.toString() == "" ||
                    editPrice.text.toString() == "" ||
                    editCount.text.toString() == ""))
        {
            setResult(RESULT_CANCELED)
        }else if(oldProductName != null &&
            oldProductName == editName.text.toString() &&
            oldProductPrice == editPrice.text.toString().toFloat() &&
            oldProductCount == editCount.text.toString().toFloat() &&
            oldBitmap == newBitmap &&
            oldProductLocation == newProductLocation)
        {
            setResult(RESULT_CANCELED)
        }else if(oldProductName == null){
            val resultIntent = Intent()
            resultIntent.putExtra(Constants.NAME_KEY,editName.text.toString())
            resultIntent.putExtra(Constants.COUNT_KEY,editCount.text.toString())
            resultIntent.putExtra(Constants.PRICE_KEY,editPrice.text.toString())

            resultIntent.putExtra(Constants.IMAGEPATH_KEY,Converters.fromBitmap(newBitmap!!))

            if(newProductLocation != null){
                resultIntent.putExtra(Constants.LATITUDE_KEY,newProductLocation?.latitude)
                resultIntent.putExtra(Constants.LONGITUDE_KEY,newProductLocation?.longitude)
            }
            setResult(RESULT_FIRST_USER,resultIntent)
        }else{
            val resultIntent = Intent()
            resultIntent.putExtra(Constants.NAME_KEY,editName.text.toString())
            resultIntent.putExtra(Constants.COUNT_KEY,editCount.text.toString())
            resultIntent.putExtra(Constants.PRICE_KEY,editPrice.text.toString())

            resultIntent.putExtra(Constants.IMAGEPATH_KEY,Converters.fromBitmap(newBitmap!!))

            resultIntent.putExtra(Constants.TABLEID_KEY,oldProductTableId)
            if(newProductLocation != null){
                resultIntent.putExtra(Constants.LATITUDE_KEY,newProductLocation?.latitude)
                resultIntent.putExtra(Constants.LONGITUDE_KEY,newProductLocation?.longitude)
            }else{
                resultIntent.putExtra(Constants.LATITUDE_KEY,oldProductLocation?.latitude)
                resultIntent.putExtra(Constants.LONGITUDE_KEY,oldProductLocation?.longitude)
            }

            setResult(2,resultIntent)
        }
        finish()
    }

    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        resultLauncher.launch(intent)
    }
}