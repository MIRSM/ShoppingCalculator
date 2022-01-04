package com.example.shoppingcalculator

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DatabaseHelper(private var context: Context?) :
    SQLiteOpenHelper(context, Constants.DATABASE_NAME,null,Constants.DATABASE_VERSION) {

    private var TABLE_NAME : String = "products"

    private var COLUMN_ID : String = "_id"
    private var COLUMN_NAME : String = "name"
    private var COLUMN_PRICE : String = "price"
    private var COLUMN_COUNT : String = "count"
    private var COLUMN_IMAGE : String = "image"
    private var COLUMN_LATITUDE : String = "latitude"
    private var COLUMN_LONGITUDE : String = "longitude"


    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_NAME TEXT, $COLUMN_PRICE REAL, $COLUMN_COUNT REAL, " +
                    "$COLUMN_IMAGE BLOB, $COLUMN_LATITUDE TEXT, $COLUMN_LONGITUDE TEXT);"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addProduct(product: Product ){
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_NAME,product.name)
        cv.put(COLUMN_PRICE,product.price)
        cv.put(COLUMN_COUNT,product.count)
        cv.put(COLUMN_IMAGE,product.imagePath)
        if(product.location != null){
            cv.put(COLUMN_LATITUDE, product.location!!.latitude)
            cv.put(COLUMN_LONGITUDE, product.location!!.longitude)
        }

        val result = db.insert(TABLE_NAME,null,cv)
        if(result.toInt() == -1){
            Toast.makeText(context,"Ошибка при добавлении в БД", Toast.LENGTH_SHORT)
        }else{
            Toast.makeText(context,"Запись успешно добавлена в БД", Toast.LENGTH_SHORT)
        }
    }

    fun getAllProducts(): Cursor? {
        val query = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase

        var cursor : Cursor? = null
        if(db != null){
            cursor = db.rawQuery(query,null)
        }
        return cursor
    }

    fun updateData(product: Product){
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COLUMN_NAME,product.name)
        cv.put(COLUMN_PRICE,product.price)
        cv.put(COLUMN_COUNT,product.count)
        cv.put(COLUMN_IMAGE,product.imagePath)
        if(product.location != null){
            cv.put(COLUMN_LATITUDE, product.location!!.latitude)
            cv.put(COLUMN_LONGITUDE, product.location!!.longitude)
        }
        val result = db.update(TABLE_NAME,cv,"_id=?",Array(1){ product.tableId.toString() })
        if(result == -1){
           Toast.makeText(context,"Не удалось обновить данные в БД",Toast.LENGTH_SHORT)
        }else{
            Toast.makeText(context,"Данные успешно обновлены в БД",Toast.LENGTH_SHORT)
        }
    }
}