package com.trionesdev.phecda.device.sdk.transformer


import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT32
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT64
import com.trionesdev.phecda.device.sdk.model.CommandValue

object CheckNaN {

    fun isNaN(cv: CommandValue): Boolean {
        when (cv.type) {
            VALUE_TYPE_FLOAT32 -> {
                val vf32: Float = cv.float32Value()
                return java.lang.Float.isNaN(vf32)
            }

            VALUE_TYPE_FLOAT64 -> {
                val vf64: Double = cv.float64Value()
                return java.lang.Double.isNaN(vf64)
            }
        }
        return false
    }

}