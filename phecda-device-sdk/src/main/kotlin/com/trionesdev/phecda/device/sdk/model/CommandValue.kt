package com.trionesdev.phecda.device.sdk.model

import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_BINARY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT32
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT64
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT16
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT32
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT64
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT8
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_UINT16
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_UINT32
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_UINT64
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_UINT8
import com.trionesdev.phecda.device.contracts.errors.CommonPhedaException
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

    fun uint8Value(): Short {
        if (this.type != VALUE_TYPE_UINT8) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_UINT8
            )
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Short
    }

    fun uint16Value(): Int {
        if (this.type != VALUE_TYPE_UINT16) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_UINT16
            )
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Int
    }

    fun uint32Value(): Long {
        if (this.type != VALUE_TYPE_UINT32) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_UINT32
            )
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Long
    }

    fun uint64Value(): Long {
        if (this.type != VALUE_TYPE_UINT64) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_UINT64
            )
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Long
    }

    fun int8Value(): Byte {
        if (this.type != VALUE_TYPE_INT8) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_INT8
            )
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Byte
    }

    fun int16Value(): Short {
        if (this.type != VALUE_TYPE_INT16) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_INT16
            )
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Short
    }

    fun int32Value(): Int {
        if (this.type != VALUE_TYPE_INT32) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_INT32
            )
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Int
    }

    fun int64Value(): Long {
        if (this.type != VALUE_TYPE_INT64) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_INT64
            )
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Long
    }

    fun float32Value(): Float {
        if (this.type != VALUE_TYPE_FLOAT32) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_FLOAT32
            )
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as Float
    }

    fun float64Value(): Double {
        if (this.type != VALUE_TYPE_FLOAT64) {
            val errMsg = java.lang.String.format(
                "cannot convert CommandValue of %s to %s",
                this.type, VALUE_TYPE_FLOAT64
            )
            throw CommonPhedaException.newCommonPhedaException(
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
            throw CommonPhedaException.newCommonPhedaException(
                KIND_SERVER_ERROR,
                errMsg,
                null
            )
        }
        return this.value as ByteArray
    }

}