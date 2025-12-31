package com.example.lab13

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyService : Service() {
    private var channel = ""
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.let {
            channel = it.getString("channel", "")
        }

        // 發送立即的歡迎訊息
        sendChannelBroadcast(
            when(channel) {
                "music" -> "歡迎來到音樂頻道"
                "new" -> "歡迎來到新聞頻道"
                "sport" -> "歡迎來到體育頻道"
                else -> "頻道錯誤"
            }
        )

        // 取消先前的任務
        job?.cancel()
        // 啟動一個新的協程來發送延遲訊息
        job = serviceScope.launch {
            delay(3000) // 延遲三秒
            sendChannelBroadcast(
                when(channel) {
                    "music" -> "即將播放本月TOP10音樂"
                    "new" -> "即將為您提供獨家新聞"
                    "sport" -> "即將播報本週NBA賽事"
                    else -> "頻道錯誤"
                }
            )
        }
        // START_STICKY 表示如果服務被系統終止，系統會嘗試重新創建服務
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // 當服務被銷毀時，取消所有協程
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent): IBinder? = null

    // 封裝發送廣播的邏輯
    private fun sendChannelBroadcast(msg: String) {
        sendBroadcast(Intent(channel).putExtra("msg", msg))
    }
}