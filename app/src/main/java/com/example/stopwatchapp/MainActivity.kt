package com.example.stopwatchapp

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startStopButton.setOnClickListener {
            startStopTimer()
        }

        resetButton.setOnClickListener {
            resetTimer()
        }

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
    }

    private fun resetTimer() {
        stopTimer()
        time = 0.0
        timeTV.text = getTimerStringFromDouble(time)
    }

    private fun startStopTimer() {
        if (timerStarted)
            stopTimer()
        else
            startTimer()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        startStopButton.text = R.string.stop_text.toString()
        startStopButton.icon = getDrawable(R.drawable.ic_baseline_pause_24)
        timerStarted = true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun stopTimer() {
        stopService(serviceIntent)
        startStopButton.text = R.string.start_text.toString()
        startStopButton.icon = getDrawable(R.drawable.ic_baseline_play_arrow_24)
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            timeTV.text = getTimerStringFromDouble(time)
        }

    }

    private fun getTimerStringFromDouble(time: Double): CharSequence? {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60
        return convertToString(hours, minutes, seconds)
    }

    private fun convertToString(hours: Int, minutes: Int, seconds: Int): CharSequence {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

}
