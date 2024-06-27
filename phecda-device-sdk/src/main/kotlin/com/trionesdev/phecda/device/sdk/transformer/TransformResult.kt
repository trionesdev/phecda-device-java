package com.trionesdev.phecda.device.sdk.transformer

import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_DOUBLE
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_LONG
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_STRING
import com.trionesdev.phecda.device.contracts.errors.CommonPhecdaException
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_NAN_ERROR
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_OVERFLOW_ERROR
import com.trionesdev.phecda.device.contracts.model.ResourceProperties
import com.trionesdev.phecda.device.sdk.model.CommandValue
import kotlin.experimental.and
import kotlin.math.ln
import kotlin.math.pow

object TransformResult {
    var defaultBase: Double = 0.0
    var defaultScale: Double = 1.0
    var defaultOffset: Double = 0.0
    var defaultMask: Long = 0
    var defaultShift: Long = 0
    var Overflow: String = "overflow"
    var NaN: String = "NaN"

    fun transformReadResult(cv: CommandValue, pv: ResourceProperties) {
        if (!isNumericValueType(cv)) {
            return
        }
        if (CheckNaN.isNaN(cv)) {
            val errMsg = String.format("NaN error for DeviceResource %s", cv.deviceResourceName)
            throw CommonPhecdaException.newCommonPhecdaException(
                KIND_NAN_ERROR,
                errMsg,
                null
            )
        }
        val value = commandValueForTransform(cv)
        var newValue = value
        if (pv.mask != null && pv.mask != defaultMask && (cv.type == VALUE_TYPE_INT || cv.type == VALUE_TYPE_LONG )) {
            newValue = transformReadMask(newValue!!, pv.mask!!)
        }
        if (pv.shift != null && pv.shift != defaultShift && (cv.type == VALUE_TYPE_INT || cv.type == VALUE_TYPE_LONG)) {
            newValue = transformReadShift(newValue!!, pv.shift!!)
        }
        if (pv.base != null && pv.base != defaultBase) {
            newValue = transformBase(newValue!!, pv.base!!, true)
        }
        if (pv.scale != null && pv.scale != defaultScale) {
            newValue = transformScale(newValue!!, pv.scale!!, true)
        }
        if (pv.offset != null && pv.offset != defaultOffset) {
            newValue = transformOffset(newValue!!, pv.offset!!, true)
        }
        if (value != newValue) {
            cv.value = newValue
        }
    }

    fun transformBase(value: Any, base: Double, read: Boolean): Any {
        var doubleValue: Double = 0.0
        when (value) {
            is Byte -> {
                doubleValue = value.toDouble()
            }

            is Short -> {
                doubleValue = value.toDouble()
            }

            is Int -> {
                doubleValue = value.toDouble()
            }

            is Long -> {
                doubleValue = value.toDouble()
            }

            is Float -> {
                doubleValue = value.toDouble()
            }

            is Double -> {
                doubleValue = value
            }
        }
        doubleValue = if (read) {
            base.pow(doubleValue)
        } else {
            ln(doubleValue) / ln(base)
        }
        val inRange = TransformValueChecker.checkTransformedValueInRange(value, doubleValue)
        if (!inRange) {
            val errMsg = String.format("transformed value out of its original type (%s) range", value)
            throw CommonPhecdaException.newCommonPhecdaException(KIND_OVERFLOW_ERROR, errMsg, null)
        }
        return when (value) {
            is Byte -> {
                doubleValue.toInt().toByte()
            }

            is Short -> {
                doubleValue.toInt().toShort()
            }

            is Int -> {
                doubleValue.toInt()
            }

            is Float -> {
                doubleValue.toFloat()
            }

            is Double -> {
                doubleValue
            }

            else -> {
                value
            }
        }
    }

    fun transformScale(value: Any, scale: Double, read: Boolean): Any {
        var valueDouble: Double? = null
        when (value) {
            is Byte -> {
                valueDouble = value.toDouble()
            }

            is Short -> {
                valueDouble = value.toDouble()
            }

            is Int -> {
                valueDouble = value.toLong().toDouble()
            }

            is Long -> {
                valueDouble = value.toDouble()
            }

            is Float -> {
                valueDouble = value.toDouble()
            }

            is Double -> {
                valueDouble = value
            }

            else -> {
                return value
            }
        }

        valueDouble = if (read) {
            valueDouble * scale
        } else {
            valueDouble / scale
        }
        val inRange = TransformValueChecker.checkTransformedValueInRange(value, valueDouble)
        if (!inRange) {
            val errMsg = String.format("transformed value out of its original type (%s) range", value)
            throw CommonPhecdaException.newCommonPhecdaException(KIND_OVERFLOW_ERROR, errMsg, null)
        }
        when (value) {
            is Byte -> {
                return if (read) {
                    value * scale
                } else {
                    value / scale
                }
            }

            is Short -> {
                return if (read) {
                    value * scale
                } else {
                    value / scale
                }
            }

            is Int -> {
                return if (read) {
                    value * scale
                } else {
                    value / scale
                }
            }

            is Long -> {
                return if (read) {
                    value * scale
                } else {
                    value / scale
                }
            }

            is Float -> {
                return if (read) {
                    value * scale
                } else {
                    value / scale
                }
            }

            is Double -> {
                return if (read) {
                    value * scale
                } else {
                    value / scale
                }
            }

            else -> {
                return value
            }
        }
    }

    fun transformOffset(value: Any, offset: Double, read: Boolean): Any {
        var valueDouble = when (value) {
            is Byte -> {
                value.toDouble()
            }

            is Short -> {
                value.toDouble()
            }

            is Int -> {
                value.toDouble()
            }

            is Float -> {
                value.toDouble()
            }

            is Double -> {
                value
            }

            else -> {
                (value as Long).toDouble()
            }
        }

        valueDouble = if (read) {
            valueDouble + offset
        } else {
            valueDouble - offset
        }
        val inRange = TransformValueChecker.checkTransformedValueInRange(value, valueDouble)
        if (!inRange) {
            val errMsg = String.format("transformed value out of its original type (%s) range", value)
            throw CommonPhecdaException.newCommonPhecdaException(KIND_OVERFLOW_ERROR, errMsg, null)
        }
        when (value) {
            is Byte -> {
                return if (read) {
                    value + offset
                } else {
                    value - offset
                }
            }

            is Short -> {
                return if (read) {
                    value + offset
                } else {
                    value - offset
                }
            }

            is Int -> {
                return if (read) {
                    value + offset
                } else {
                    value - offset
                }
            }

            is Float -> {
                return if (read) {
                    value + offset
                } else {
                    value - offset
                }
            }

            is Double -> {
                return if (read) {
                    value + offset
                } else {
                    value - offset
                }
            }

            else -> {
                return value
            }
        }
    }

    fun transformReadMask(value: Any, mask: Long): Any {

        return when (value) {
            is Short -> {
                value.toShort() and mask.toShort()
            }

            is Int -> {
                value.toLong() and mask
            }

            is Long -> {
                value and mask
            }

            else -> {
                value
            }
        }

    }

    fun transformReadShift(value: Any, shift: Long): Any {
        return when (value) {
            is Int -> {
                if (shift > 0) {
                    value shl shift.toInt()
                } else {
                    value shr -shift.toInt()
                }
            }

            else -> {
                value
            }
        }
    }

    fun commandValueForTransform(cv: CommandValue?): Any? {
        when (cv?.type!!) {
            VALUE_TYPE_INT -> {
                return cv.intValue()
            }

            VALUE_TYPE_LONG -> {
                return cv.longValue()
            }

            VALUE_TYPE_FLOAT -> {
                return cv.floatValue()
            }

            VALUE_TYPE_DOUBLE -> {
                return cv.doubleValue()
            }

            else -> {
                return null
            }
        }
    }

    fun mapCommandValue(value: CommandValue, mappings: Map<String, String>?): CommandValue? {
        var result: CommandValue? = null
        mappings?.let {
            it[value.valueToString()]
        }?.let {
            if (it.isNotBlank()) {
                result = CommandValue.newCommandValue(value.deviceResourceName!!, VALUE_TYPE_STRING, it)
            }
        }
        return result
    }

    fun isNumericValueType(cv: CommandValue): Boolean {
        return when (cv.type) {
            VALUE_TYPE_INT, VALUE_TYPE_LONG, VALUE_TYPE_FLOAT, VALUE_TYPE_DOUBLE -> true
            else -> false
        }
    }

}