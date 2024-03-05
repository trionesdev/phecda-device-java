package com.trionesdev.phecda.device.sdk.transformer

import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_DOUBLE
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT
import com.trionesdev.phecda.device.sdk.model.CommandValue

object CheckNaN {

    fun isNaN(cv: CommandValue): Boolean {
        when (cv.type) {
            VALUE_TYPE_FLOAT -> {
                val vf32: Float = cv.floatValue()
                return java.lang.Float.isNaN(vf32)
            }

            VALUE_TYPE_DOUBLE -> {
                val vf64: Double = cv.doubleValue()
                return java.lang.Double.isNaN(vf64)
            }
        }
        return false
    }

}