package com.example.lab_16

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lab_16.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var items: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: SQLiteDatabase
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 取得資料庫實體
        dbrw = MyDBHelper(this).writableDatabase
        // 宣告Adapter並連結ListView
        adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, items)
        binding.listView.adapter = adapter
        // 設定監聽器
        setListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbrw.close() // 關閉資料庫
    }

    // 設定監聽器
    private fun setListener() {
        binding.btnInsert.setOnClickListener {
            val book = binding.edBook.text.toString()
            val price = binding.edPrice.text.toString()
            // 判斷是否有填入書名或價格
            if (book.isEmpty() || price.isEmpty())
                showToast("欄位請勿留空")
            else
                try {
                    // 新增一筆書籍紀錄於myTable資料表
                    dbrw.execSQL(
                        "INSERT INTO myTable(book, price) VALUES(?,?)",
                        arrayOf(book, price)
                    )
                    showToast("新增:$book, 價格:$price")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("新增失敗:$e")
                }
        }
        binding.btnUpdate.setOnClickListener {
            val book = binding.edBook.text.toString()
            val price = binding.edPrice.text.toString()
            // 判斷是否有填入書名或價格
            if (book.isEmpty() || price.isEmpty())
                showToast("欄位請勿留空")
            else
                try {
                    // 尋找相同書名的紀錄並更新price欄位的值
                    dbrw.execSQL("UPDATE myTable SET price = ? WHERE book LIKE ?", arrayOf(price, book))
                    showToast("更新:$book, 價格:$price")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("更新失敗:$e")
                }
        }
        binding.btnDelete.setOnClickListener {
            val book = binding.edBook.text.toString()
            // 判斷是否有填入書名
            if (book.isEmpty())
                showToast("書名請勿留空")
            else
                try {
                    // 從myTable資料表刪除相同書名的紀錄
                    dbrw.execSQL("DELETE FROM myTable WHERE book LIKE ?", arrayOf(book))
                    showToast("刪除:$book")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("刪除失敗:$e")
                }
        }
        binding.btnQuery.setOnClickListener {
            val book = binding.edBook.text.toString()
            val c = if (book.isEmpty())
                dbrw.rawQuery("SELECT * FROM myTable", null)
            else
                dbrw.rawQuery("SELECT * FROM myTable WHERE book LIKE ?", arrayOf(book))

            items.clear()
            showToast("共有${c.count}筆資料")
            c.use { // 使用 use 區塊確保 Cursor 會自動關閉
                if (it.moveToFirst()) {
                    do {
                        val bookNameIndex = it.getColumnIndex("book")
                        val priceIndex = it.getColumnIndex("price")

                        val bookName = if (bookNameIndex != -1) it.getString(bookNameIndex) else ""
                        val priceValue = if(priceIndex != -1) it.getInt(priceIndex) else 0
                        
                        items.add("書名:$bookName\t\t\t\t價格:$priceValue")
                    } while (it.moveToNext())
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    // 建立showToast方法顯示Toast訊息
    private fun showToast(text: String) =
        Toast.makeText(this,text, Toast.LENGTH_LONG).show()

    // 清空輸入的書名與價格
    private fun cleanEditText() {
        binding.edBook.setText("")
        binding.edPrice.setText("")
    }
}
