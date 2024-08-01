package com.example.animationdemo.wheel

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearSmoothScroller

class CenterLinearSmoothScroller(context: Context?) : LinearSmoothScroller(context) {


    override fun calculateDtToFit(
        viewStart: Int,
        viewEnd: Int,
        boxStart: Int,
        boxEnd: Int,
        snapPreference: Int
    ): Int {
        val childCenter = viewStart + ((viewEnd - viewStart) / 2)
        val containerCenter = boxStart + ((boxEnd - boxStart) / 2)
        Log.i("CenterLinearSmoothScroller","containerCenter:$containerCenter,childCenter:$childCenter,")
        return containerCenter - childCenter
    }
}