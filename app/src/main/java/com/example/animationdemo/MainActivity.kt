package com.example.animationdemo

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animationdemo.wheel.RecyclerWheelView
import com.example.animationdemo.wheel.RecyclerWheelDataAdapter
import com.example.animationdemo.wheel.WheelSlideListener
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val wheelView = findViewById<RecyclerWheelView>(R.id.wheel_view)
        val adapter = RecyclerWheelDataAdapter(20, 6, 6)
        wheelView.adapter = adapter
        adapter.itemClick = {
            wheelView.select(it, true)
        }
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        wheelView.layoutManager = layoutManager
        wheelView.wheelSlideListener = WheelSlideListener.SimpleWheelSlideListener(this,layoutManager)

        findViewById<Button>(R.id.test_btn).setOnClickListener {
            val p = Random.nextInt(10)
            wheelView.select(p, p / 2 == 0)
        }
    }
}