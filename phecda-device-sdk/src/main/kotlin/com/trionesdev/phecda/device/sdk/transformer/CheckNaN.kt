package com.trionesdev.phecda.device.sdk.transformer

import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeDouble
import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeFloat
import com.trionesdev.phecda.device.sdk.model.CommandValue

object CheckNaN {

    fun isNaN(cv: CommandValue): Boolean {
        when (cv.type) {
            ValueTypeFloat -> {
                val vf32: Float = cv.floatValue()
                return java.lang.Float.isNaN(vf32)
            }

            ValueTypeDouble -> {
                val vf64: Double = cv.doubleValue()
                return java.lang.Double.isNaN(vf64)
            }
        }
        return false
    }

}