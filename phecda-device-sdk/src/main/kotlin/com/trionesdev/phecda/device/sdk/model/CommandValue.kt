package com.trionesdev.phecda.device.sdk.model

import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_BINARY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_DOUBLE
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_LONG
import com.trionesdev.phecda.device.contracts.errors.CommonPhecdaException
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_SERVER_ERROR

class CommandValue {
    companion object {
        @JvmStatic
        fun newCommandValue(deviceResourceName: String?, valueType: String?, value: Any): CommandValue {
            return CommandValue().apply {
                this.deviceResourceName = deviceResourceName
                this.type = valueType
                this.value = value
            }
        }

        @JvmStatic
        fun newCommandValueWithOrigin(deviceResourceName: String?, valueType: String?, value: Any, origin: Long): CommandValue {
            return CommandValue().apply {
                this.deviceResourceName = deviceResourceName
                this.type = valueType
                this.value = value
                this.origin = origin
            }
        }

    }

    var deviceResourceName: String? = null
    var type: String? = null
    var value: Any? = null
    var origin: Long? = null
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

    fun intValue(): Short {
        if (this.type != VALUE_TYPE_INT) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_INT
            )
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Short
    }

    fun longValue(): Long {
        if (this.type != VALUE_TYPE_LONG) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_LONG
            )
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Long
    }

    fun floatValue(): Float {
        if (this.type != VALUE_TYPE_FLOAT) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_FLOAT
            )
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Float
    }

    fun doubleValue(): Double {
        if (this.type != VALUE_TYPE_DOUBLE) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_DOUBLE
            )
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Double
    }

    fun binaryValue(): ByteArray {
        if (this.type != VALUE_TYPE_BINARY) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_BINARY
            )
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as ByteArray
    }

}