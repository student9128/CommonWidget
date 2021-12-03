package com.kevin.commonwidgetlib

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import com.kevin.commonwidget.CircleWaveView
import com.kevin.commonwidget.ToggleView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cwv = findViewById<CircleWaveView>(R.id.cwv)
        cwv.mProgress = 75
        cwv.mWaveCount = 10
        cwv.mProgressColor = ContextCompat.getColor(this,R.color.black)
//        cwv.mWaveColor=Color.parseColor("#FFAB4C")
        cwv.mBackgroundColor=Color.parseColor("#F90716")
//        cwv.setMyBackgroundColor(Color.parseColor("#F90716"))
//        cwv.waveColor = Color.parseColor("#FFAFAF")
        cwv.mWaveColor = Color.parseColor("#FFAFAF")
        val tv = findViewById<ToggleView>(R.id.tv)
        tv.checkedColor = Color.parseColor("#344CB7")
    }
}