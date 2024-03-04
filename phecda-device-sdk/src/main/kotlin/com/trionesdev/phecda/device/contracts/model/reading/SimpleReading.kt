package com.trionesdev.phecda.device.contracts.model.reading

import com.alibaba.fastjson2.JSON
import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeArray
import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeBool
import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeDouble
import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeFloat
import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeInt
import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeString
import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeStruct
import com.trionesdev.phecda.device.contracts.model.reading.BaseReading.Companion.newBaseReading
import java.text.MessageFormat

open class SimpleReading : Reading {
    companion object {
        fun newSimpleReading(
            profileName: String?,
            deviceName: String?,
            resourceName: String?,
            valueType: String?,
            value: Any?
        ): BaseReading {
            val stringValue: String = convertInterfaceValue(
                valueType,
                value
            ).toString()
            return newBaseReading(profileName, deviceName, resourceName, valueType).apply {
                this.value = stringValue
            }
        }

        fun convertInterfaceValue(valueType: String?, value: Any?): String? {
            return when (valueType) {
                ValueTypeBool -> {
                    if (value as Boolean) {
                        return "true"
                    } else {
                        return "false"
                    }
                }

                ValueTypeInt, ValueTypeFloat, ValueTypeDouble -> value.toString()
                ValueTypeString -> value.toString()
                ValueTypeStruct, ValueTypeArray -> JSON.toJSONString(value)
                else -> throw RuntimeException(MessageFormat.format("invalid simple reading type of {}", valueType))
            }
        }

    }

    val value: String? = null
}