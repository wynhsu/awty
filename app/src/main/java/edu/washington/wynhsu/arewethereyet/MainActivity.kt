package edu.washington.wynhsu.arewethereyet

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart.setOnClickListener {
            val btn = btnStart.text
            if (validate() && btn == "Start") {
                btnStart.text = "Stop"
                val msg = txtMsg.text
                val numb = txtPhone.text
                val count = txtMin.text.toString().toDouble()
                val toast = numb.toString() + ": " + msg

                val intent = Intent("edu.washington.wynhsu.arewethereyet").apply {
                    putExtra("message", toast)
                }
                val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

                sendBroadcast(intent)

                val intentFilter = IntentFilter("edu.washington.wynhsu.arewethereyet")
                registerReceiver(AlarmReceiver(), intentFilter)

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + (count * 1000).toLong(), (count * 1000).toLong(), pendingIntent)
            } else if (btn == "Stop") {
                btnStart.text = "Start"
                unregisterReceiver(AlarmReceiver())
            } else {
                Log.i("error", "can't have empty fields")
            }
        }
    }

    fun validate(): Boolean{
        val msg = txtMsg.text
        val numb = txtPhone.text
        val count = txtMin
        return (msg.toString() != "" && numb.toString() != "" && count.toString() != "")
    }
}

class AlarmReceiver(): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val msg = intent!!.getStringExtra("message")
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    }
}