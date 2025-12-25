package com.example.lab16_2

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lab16_2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val items = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private val uri = Uri.parse("content://com.example.lab16")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        binding.listView.adapter = adapter

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.btnInsert.setOnClickListener {
            val name = binding.edBook.text.toString()
            val price = binding.edPrice.text.toString()

            if (name.isEmpty() || price.isEmpty()) {
                showToast("欄位請勿留空")
            } else {
                val values = ContentValues().apply {
                    put("book", name)
                    put("price", price)
                }
                contentResolver.insert(uri, values)?.also {
                    showToast("新增:$name, 價格:$price")
                    cleanEditText()
                } ?: showToast("新增失敗")
            }
        }

        binding.btnUpdate.setOnClickListener {
            val name = binding.edBook.text.toString()
            val price = binding.edPrice.text.toString()

            if (name.isEmpty() || price.isEmpty()) {
                showToast("欄位請勿留空")
            } else {
                val values = ContentValues().apply {
                    put("price", price)
                }
                val count = contentResolver.update(uri, values, name, null)
                if (count > 0) {
                    showToast("更新:$name, 價格:$price")
                    cleanEditText()
                } else {
                    showToast("更新失敗")
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            val name = binding.edBook.text.toString()
            if (name.isEmpty()) {
                showToast("書名請勿留空")
            } else {
                val count = contentResolver.delete(uri, name, null)
                if (count > 0) {
                    showToast("刪除:$name")
                    cleanEditText()
                } else {
                    showToast("刪除失敗")
                }
            }
        }

        binding.btnQuery.setOnClickListener {
            val name = binding.edBook.text.toString()
            val selection = name.ifEmpty { null }
            contentResolver.query(uri, null, selection, null, null)?.use { cursor ->
                items.clear()
                showToast("共有${cursor.count}筆資料")
                while (cursor.moveToNext()) {
                    val bookName = cursor.getString(0)
                    val bookPrice = cursor.getInt(1)
                    items.add("書名:$bookName\t\t\t\t價格:$bookPrice")
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun cleanEditText() {
        binding.edBook.text.clear()
        binding.edPrice.text.clear()
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}