package com.trionesdev.phecda.device.sdk.transformer

import com.trionesdev.phecda.device.contracts.errors.CommonPhecdaException
import com.trionesdev.phecda.device.contracts.errors.ErrorKind.KIND_CONTRACT_INVALID
import com.trionesdev.phecda.device.contracts.model.ResourceProperties
import com.trionesdev.phecda.device.sdk.model.CommandValue

object TransformParam {
    fun transformWriteParameter(cv: CommandValue, pv: ResourceProperties?) {
        if (!TransformResult.isNumericValueType(cv)) {
            return
        }
        val value = TransformResult.commandValueForTransform(cv)
        var newValue = value
        pv?.maximum?.let {
            validateWriteMaximum(value, pv.maximum!!)
        }
        pv?.minimum?.let {
            validateWriteMinimum(value, pv.minimum!!)
        }
        pv?.offset?.let {
            if (it != TransformResult.defaultOffset) {
                newValue = TransformResult.transformOffset(newValue!!, it, false)
            }
        }
        pv?.scale?.let {
            if (pv.scale != TransformResult.defaultScale) {
                newValue = TransformResult.transformScale(newValue!!, pv.scale!!, false)
            }
        }
        pv?.base?.let {
            if (it != TransformResult.defaultBase) {
                newValue = TransformResult.transformBase(newValue!!, it, false)
            }
        }
        if (value != newValue) {
            cv.value = newValue
        }
    }

    fun validateWriteMaximum(value: Any?, maximum: Double) {
        when (value) {
            is Byte -> {
                if (value > maximum) {
                    val errMsg = String.format("set command parameter out of maximum value %s", maximum)
                    throw CommonPhecdaException(KIND_CONTRACT_INVALID, errMsg)
                }
            }

            is Short -> {
                if (value > maximum) {
                    val errMsg = String.format("set command parameter out of maximum value %s", maximum)
                    throw CommonPhecdaException(KIND_CONTRACT_INVALID, errMsg)
                }
            }

            is Int -> {
                if (value > maximum) {
                    val errMsg = String.format("set command parameter out of maximum value %s", maximum)
                    throw CommonPhecdaException(KIND_CONTRACT_INVALID, errMsg)
                }
            }

            is Long -> {
                if (value > maximum) {
                    val errMsg = String.format("set command parameter out of maximum value %s", maximum)
                    throw CommonPhecdaException(KIND_CONTRACT_INVALID, errMsg)
                }
            }
        }
    }

    fun validateWriteMinimum(value: Any?, minimum: Double) {
        when (value) {
            is Byte -> {
                if (value < minimum) {
                    val errMsg = String.format("set command parameter out of minimum value %s", minimum)
                    throw CommonPhecdaException(KIND_CONTRACT_INVALID, errMsg)
                }
            }

            is Short -> {
                if (value < minimum) {
                    val errMsg = String.format("set command parameter out of minimum value %s", minimum)
                    throw CommonPhecdaException(KIND_CONTRACT_INVALID, errMsg)
                }
            }

            is Int -> {
                if (value < minimum) {
                    val errMsg = String.format("set command parameter out of minimum value %s", minimum)
                    throw CommonPhecdaException(KIND_CONTRACT_INVALID, errMsg)
                }
            }

            is Long -> {
                if (value < minimum) {
                    val errMsg = String.format("set command parameter out of minimum value %s", minimum)
                    throw CommonPhecdaException(KIND_CONTRACT_INVALID, errMsg)
                }
            }
        }
    }
}