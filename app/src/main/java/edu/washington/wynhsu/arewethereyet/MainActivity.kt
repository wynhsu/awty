package edu.washington.wynhsu.arewethereyet

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var phone: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissions()
    }

    override fun onResume() {
        super.onResume()
        btnStart.setOnClickListener {
            if (validate()) {
                val btn = btnStart.text.toString()
                btnStart.text = "Stop"
                val msg = txtMsg.text.toString()
                phone = txtPhone.text.toString()
                val count = txtMin.text.toString().toInt()

                val intent = Intent("edu.washington.wynhsu.arewethereyet").apply {
                    removeExtra("message")
                    putExtra("message", msg)
                }
                val intentFilter = IntentFilter("edu.washington.wynhsu.arewethereyet")
                registerReceiver(AlarmReceiver(phone), intentFilter)
                val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                if (btn == "Start") {
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + (count * 1000).toLong(),
                            (count * 1000).toLong(), pendingIntent)
                } else {
                    btnStart.text = "Start"
                    alarmManager.cancel(pendingIntent)
//                    Log.i("end", "canceled")
                }
            } else {
                Toast.makeText(this, "Can't have empty fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(AlarmReceiver(phone))
    }

    private fun validate(): Boolean{
        val msg = txtMsg.text.toString()
        val numb = txtPhone.text.toString()
        val count = txtMin.toString()
        return (msg != "" && numb != "" && count != "")
    }

    private fun permissions() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), 0)
        }
    }
}

class AlarmReceiver(private val phone: String) : BroadcastReceiver() {
    private val sms = SmsManager.getDefault()
    override fun onReceive(context: Context?, intent: Intent?) {
        val msg = intent!!.getStringExtra("message")
        sms.sendTextMessage(phone, null, msg, null, null)
        Toast.makeText(context, "$phone: $msg", Toast.LENGTH_SHORT).show()
//        Log.i("alarm call", msg)
    }
}