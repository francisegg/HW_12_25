package com.example.lab14

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

// 繼承 OnMapReadyCallback
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 0
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 權限允許，重新讀取地圖
                    loadMap()
                } else {
                    // 權限被拒，結束應用程式
                    finish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 讀取地圖
        loadMap()
    }

    // 實作 onMapReady，當地圖準備好時會呼叫此方法
    override fun onMapReady(map: GoogleMap) {
        // 檢查是否允許精確位置權限
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 顯示目前位置與目前位置的按鈕
            map.isMyLocationEnabled = true
            // 加入標記
            val taipei101 = LatLng(25.033611, 121.565000)
            map.addMarker(MarkerOptions()
                .position(taipei101)
                .title("台北101")
                .draggable(true))

            val taipeiStation = LatLng(25.047924, 121.517081)
            map.addMarker(MarkerOptions()
                .position(taipeiStation)
                .title("台北車站")
                .draggable(true))

            // 繪製線段
            map.addPolyline(PolylineOptions()
                .add(taipei101)
                .add(LatLng(25.032435, 121.534905))
                .add(taipeiStation)
                .color(Color.BLUE)
                .width(10f))
            
            // 移動視角
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(25.035, 121.54), 13f))
        } else {
            // 請求權限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    // 讀取地圖
    private fun loadMap() {
        // 取得 SupportMapFragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        // 使用非同步的方式取得地圖
        mapFragment.getMapAsync(this)
    }
}