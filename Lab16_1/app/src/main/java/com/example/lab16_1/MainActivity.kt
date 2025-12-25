package com.example.lab16_1

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val items = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbrw = MyDBHelper(this).writableDatabase

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter

        setListener()
        query() // 初始載入資料
    }

    override fun onDestroy() {
        super.onDestroy()
        dbrw.close()
    }

    private fun setListener() {
        val edBook = findViewById<EditText>(R.id.edBook)
        val edPrice = findViewById<EditText>(R.id.edPrice)

        findViewById<Button>(R.id.btnInsert).setOnClickListener {
            val book = edBook.text.toString()
            val price = edPrice.text.toString()

            if (book.isEmpty() || price.isEmpty()) {
                showToast("欄位請勿留空")
            } else {
                try {
                    dbrw.execSQL(
                        "INSERT INTO myTable(book, price) VALUES(?,?)",
                        arrayOf(book, price)
                    )
                    showToast("新增:$book, 價格:$price")
                    cleanEditText()
                    query()
                } catch (e: Exception) {
                    showToast("新增失敗:$e")
                }
            }
        }

        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            val book = edBook.text.toString()
            val price = edPrice.text.toString()

            if (book.isEmpty() || price.isEmpty()) {
                showToast("欄位請勿留空")
            } else {
                try {
                    dbrw.execSQL("UPDATE myTable SET price = ? WHERE book = ?", arrayOf(price, book))
                    showToast("更新:$book, 價格:$price")
                    cleanEditText()
                    query()
                } catch (e: Exception) {
                    showToast("更新失敗:$e")
                }
            }
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            val book = edBook.text.toString()
            if (book.isEmpty()) {
                showToast("書名請勿留空")
            } else {
                try {
                    dbrw.execSQL("DELETE FROM myTable WHERE book = ?", arrayOf(book))
                    showToast("刪除:$book")
                    cleanEditText()
                    query()
                } catch (e: Exception) {
                    showToast("刪除失敗:$e")
                }
            }
        }

        findViewById<Button>(R.id.btnQuery).setOnClickListener {
            query()
        }
    }

    private fun query() {
        val edBook = findViewById<EditText>(R.id.edBook)
        val book = edBook.text.toString()
        val args = if (book.isNotEmpty()) arrayOf(book) else null
        val queryString = if (book.isNotEmpty())
            "SELECT * FROM myTable WHERE book = ?"
        else
            "SELECT * FROM myTable"

        val newItems = ArrayList<String>()
        dbrw.rawQuery(queryString, args).use { c ->
            showToast("共有${c.count}筆資料")
            if (c.moveToFirst()) {
                val bookColumnIndex = c.getColumnIndex("book")
                val priceColumnIndex = c.getColumnIndex("price")
                if (bookColumnIndex != -1 && priceColumnIndex != -1) {
                    do {
                        val bookName = c.getString(bookColumnIndex)
                        val priceValue = c.getInt(priceColumnIndex)
                        newItems.add("書名:$bookName\t\t\t\t價格:$priceValue")
                    } while (c.moveToNext())
                }
            }
        }
        items.clear()
        items.addAll(newItems)
        adapter.notifyDataSetChanged()
    }

    private fun showToast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun cleanEditText() {
        findViewById<EditText>(R.id.edBook).setText("")
        findViewById<EditText>(R.id.edPrice).setText("")
    }
}