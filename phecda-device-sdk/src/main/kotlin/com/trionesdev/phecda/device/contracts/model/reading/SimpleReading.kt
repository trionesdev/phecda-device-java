package com.trionesdev.phecda.device.contracts.model.reading

import com.alibaba.fastjson2.JSON
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_BOOL
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_DOUBLE
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_STRING
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_STRUCT
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
                VALUE_TYPE_BOOL -> {
                    if (value as Boolean) {
                        return "true"
                    } else {
                        return "false"
                    }
                }

                VALUE_TYPE_INT, VALUE_TYPE_FLOAT, VALUE_TYPE_DOUBLE -> value.toString()
                VALUE_TYPE_STRING -> value.toString()
                VALUE_TYPE_STRUCT, VALUE_TYPE_ARRAY -> JSON.toJSONString(value)
                else -> throw RuntimeException(MessageFormat.format("invalid simple reading type of {}", valueType))
            }
        }

    }

    val value: String? = null
}