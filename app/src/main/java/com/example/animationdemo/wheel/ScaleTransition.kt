package com.example.animationdemo.wheel

class ScaleTransition(private val startScale: Float, private val endScale: Float) {

    // 根据插值比例计算缩放值
    fun calculateColor(fraction: Float): Float {
        val offset = endScale - startScale
        return startScale + offset * fraction
    }
}