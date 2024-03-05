package com.trionesdev.phecda.device.sdk.model

import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeDouble
import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeFloat
import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeInt
import com.trionesdev.phecda.device.contracts.errors.CommonPhedaException
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_SERVER_ERROR

class CommandValue {
    companion object {
        fun newCommandValue(deviceResourceName: String, valueType: String, value: Any): CommandValue {
            return CommandValue().apply {
                this.deviceResourceName = deviceResourceName
                this.type = valueType
                this.value = value
            }
        }

    }

    var deviceResourceName: String? = null
    var type: String? = null
    var value: Any? = null
    val origin: Long? = null
    val tags: Map<String, String>? = null

    fun valueToString(): String {
        return value.toString()
    }

    fun string(): String {
        return java.lang.String.format(
            "DeviceResource: %s, %s: %s",
            this.deviceResourceName,
            this.type,
            this.valueToString()
        )
    }

    fun intValue(): Int {
        if (this.type != ValueTypeInt) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, ValueTypeInt
            )
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Int
    }

    fun floatValue(): Float {
        if (this.type != ValueTypeFloat) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, ValueTypeFloat
            )
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Float
    }

    fun doubleValue(): Double {
        if (this.type != ValueTypeDouble) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, ValueTypeDouble
            )
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Double
    }

}