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
            if (validate()) {
                val btn = btnStart.text.toString()
                btnStart.text = "Stop"
                val msg = txtMsg.text.toString()
                val numb = format(txtPhone.text.toString())
                val count = txtMin.text.toString().toInt()
                val toast = "$numb: $msg"

                val intent = Intent("edu.washington.wynhsu.arewethereyet").apply {
                    putExtra("message", toast)
                }
                val intentFilter = IntentFilter("edu.washington.wynhsu.arewethereyet")
                registerReceiver(AlarmReceiver(), intentFilter)
                val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                if (btn == "Start") {
                    sendBroadcast(intent)
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + (count * 1000).toLong(),
                            (count * 1000).toLong(), pendingIntent)
                } else {
                    btnStart.text = "Start"
                    alarmManager.cancel(pendingIntent)
                }
            } else {
                Toast.makeText(this, "Can't have empty fields!", Toast.LENGTH_SHORT).show()
                Log.i("error", "can't have empty fields")
            }
        }
    }

    fun validate(): Boolean{
        val msg = txtMsg.text.toString()
        val numb = txtPhone.text.toString()
        val count = txtMin.toString()
        return (msg != "" && numb != "" && count != "")
    }

    fun format(s: String): String {
        val onethree = s.substring(0, 3)
        val foursix = s.substring(3, 6)
        val seventen = s.substring(6, s.length)
        return "($onethree) $foursix-$seventen"
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val msg = intent!!.getStringExtra("message")
        Log.i("alarm call", "$msg")
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}