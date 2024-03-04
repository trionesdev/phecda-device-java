package com.trionesdev.phecda.device.sdk.transformer

object TransformValueChecker {
    fun checkTransformedValueInRange(origin: Any?, transformed: Double): Boolean {
        var inRange = false
        when (origin) {
            is Int -> {
                if (transformed >= Int.MIN_VALUE && transformed <= Int.MAX_VALUE) {
                    inRange = true
                }
            }
        }
        return inRange
    }
}