package com.example.animationdemo.wheel

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animationdemo.R

interface WheelSlideListener {

    fun onSliding(
        forward: Boolean,
        selectedPosition: Int,
        prevFraction: Float,
        fraction: Float,
        nextFraction: Float
    )

    fun onSelected(selectedPosition: Int)


    class SimpleWheelSlideListener(
        val context: Context,
        val layoutManager: LinearLayoutManager
    ) : WheelSlideListener {

        private val scaleTransition by lazy {
            ScaleTransition(1f, 3f)
        }

        private val alphaTransition by lazy {
            Transition.UpdateDialogTextAlphaTransition(
                0.5f,
                1f
            )
        }

        override fun onSliding(
            forward: Boolean,
            selectedPosition: Int,
            prevFraction: Float,
            fraction: Float,
            nextFraction: Float
        ) {
            val selectView = layoutManager.findViewByPosition(selectedPosition) ?: return
            val next = if (forward) selectedPosition + 1 else selectedPosition - 1
            val prev = if (forward) selectedPosition - 1 else selectedPosition + 1
            val nextView = layoutManager.findViewByPosition(next) ?: return
            val prevView = layoutManager.findViewByPosition(prev) ?: return
            setWheelTextFontColor(selectView, fraction)
            setWheelTextFontColor(nextView, nextFraction)
            setWheelTextFontColor(prevView, prevFraction)
        }

        override fun onSelected(selectedPosition: Int) {

        }

        private fun setWheelTextFontColor(view: View, fraction: Float) {
            val wheelView = view.findViewById<TextView>(R.id.wheel_text)
//            wheelView.textSize = fontTransition.calculate(fraction)
            wheelView.scaleX = scaleTransition.calculateColor(fraction)
            wheelView.scaleY = scaleTransition.calculateColor(fraction)
            wheelView.translationX = (fraction -1f) * 200
            wheelView.alpha = alphaTransition.calculate(fraction)
        }


    }
}