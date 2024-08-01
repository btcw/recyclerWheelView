package com.example.animationdemo.wheel

import android.content.Context
import androidx.core.content.ContextCompat

abstract class Transition<T, K>(
    //起始值
    startValue: T,
    //结束值
    endValue: T
) {

    abstract fun calculate(fraction: Float): K

    class UpdateDialogFontTransition(
        private val context: Context,
        private val startValue: Int,
        private val endValue: Int
    ) :
        Transition<Int, Float>(startValue, endValue) {
        override fun calculate(fraction: Float): Float {
            val startDimension = context.resources.getDimension(startValue)
            val endDimension = context.resources.getDimension(endValue)
            return startDimension + (endDimension - startDimension) * fraction
        }
    }

    class UpdateDialogTextAlphaTransition(
        private val startValue: Float,
        private val endValue: Float
    ) : Transition<Float,Float>(startValue, endValue) {

        override fun calculate(fraction: Float): Float {
            return startValue + (endValue - startValue) * fraction
        }
    }
}
