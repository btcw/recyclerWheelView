package com.example.animationdemo.wheel

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator


class BounceEaseInOutInterpolator : Interpolator {

    private val mEaseInOutInterpolator: Interpolator = AccelerateDecelerateInterpolator()
    private val mBounceInterpolator: Interpolator = BounceInterpolator()

    companion object {
        // 弹跳效果开始的阈值
        private const val BOUNCE_THRESHOLD: Float = 0.05f
    }


    override fun getInterpolation(input: Float): Float {
        // 使用ease-in-out插值器处理大部分动画
        var result = mEaseInOutInterpolator.getInterpolation(input)

        // 当动画接近结束时，应用弹跳效果
        if (result >= 1.0f - BOUNCE_THRESHOLD) {
            result = 1.0f - mBounceInterpolator.getInterpolation(1.0f - result)
        }
        return result
    }
}