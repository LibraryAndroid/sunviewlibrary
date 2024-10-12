package com.library.sunview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val sunView = findViewById<SunView>(R.id.sunView)
        sunView.apply {
            setStartTime("6:05")
            setEndTime("18:01")
            setCurrentTime("17:01")
            setArcSolidColor(ContextCompat.getColor(context, R.color.ArcSolidColor))
            setArcColor(ContextCompat.getColor(context, R.color.ArcColor))
            setBottomLineColor(ContextCompat.getColor(context, R.color.BottomLineColor))
            setTimeTextColor(ContextCompat.getColor(context, R.color.TimeTextColor))
            setSunColor(ContextCompat.getColor(context, R.color.SunColor))
            setIsp24HourFormat(false)
        }
    }
}