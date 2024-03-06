package com.trionesdev.phecda.device.contracts.model.reading

import com.alibaba.fastjson2.JSON
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_BOOL
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_BOOL_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT32
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT32_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT64
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_FLOAT64_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT16
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT16_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT32
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT32_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT64
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT64_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT8
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_INT8_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_STRING
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_STRING_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_UINT16
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_UINT16_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_UINT32
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_UINT32_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_UINT64
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_UINT64_ARRAY
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_UINT8
import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_UINT8_ARRAY
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

                VALUE_TYPE_UINT8, VALUE_TYPE_UINT16,
                VALUE_TYPE_UINT32, VALUE_TYPE_UINT64,
                VALUE_TYPE_INT8, VALUE_TYPE_INT16,
                VALUE_TYPE_INT32, VALUE_TYPE_INT64,
                VALUE_TYPE_FLOAT32, VALUE_TYPE_FLOAT64 -> value.toString()

                VALUE_TYPE_STRING -> value.toString()
                VALUE_TYPE_BOOL_ARRAY, VALUE_TYPE_STRING_ARRAY,
                VALUE_TYPE_UINT8_ARRAY, VALUE_TYPE_UINT16_ARRAY,
                VALUE_TYPE_UINT32_ARRAY, VALUE_TYPE_UINT64_ARRAY,
                VALUE_TYPE_INT8_ARRAY, VALUE_TYPE_INT16_ARRAY,
                VALUE_TYPE_INT32_ARRAY, VALUE_TYPE_INT64_ARRAY,
                VALUE_TYPE_FLOAT32_ARRAY, VALUE_TYPE_FLOAT64_ARRAY -> JSON.toJSONString(value)

                else -> throw RuntimeException(MessageFormat.format("invalid simple reading type of {}", valueType))
            }
        }

    }

    val value: String? = null
}