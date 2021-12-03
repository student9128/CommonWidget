package com.kevin.commonwidgetlib

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.kevin.commonwidget.CircleWaveView
import com.kevin.commonwidget.ToggleView
import com.kevin.commonwidget.util.LogUtils

class MainActivity : AppCompatActivity() {
    var hello: WaveViewX? = null
    var isAnim = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cwv = findViewById<CircleWaveView>(R.id.cwv)
        cwv.mProgress = 75
        cwv.mWaveCount = 10
        cwv.mProgressColor = ContextCompat.getColor(this, R.color.black)
//        cwv.mWaveColor=Color.parseColor("#FFAB4C")
        cwv.mBackgroundColor = Color.parseColor("#F90716")
//        cwv.setMyBackgroundColor(Color.parseColor("#F90716"))
//        cwv.waveColor = Color.parseColor("#FFAFAF")
        cwv.mWaveColor = Color.parseColor("#FFAFAF")
        val tv = findViewById<ToggleView>(R.id.tv)
        tv.checkedColor = Color.parseColor("#344CB7")
//        hello = findViewById<WaveViewX>(R.id.hello)
        val sb = findViewById<Button>(R.id.sb)
        sb.setOnClickListener {
            val x = cwv.getProgress()
            var xx = x + 1
            cwv.setProgress(xx)

//            isAnim = if (isAnim) {
//                cwv.stopAnim()
//                false
//            } else {
//                cwv.startAnim()
//                true
//            }
        }

    }

    override fun onResume() {
        super.onResume()

//        hello!!.percent = 30
//        hello!!.lifeDelegate = WaveViewX.RESUME
//        hello!!.setTextColor(Color.parseColor("#344CB7"))

    }

    override fun onStop() {
        super.onStop()
//        hello!!.lifeDelegate = WaveViewX.STOP
    }


}