package com.trionesdev.phecda.device.contracts.model.reading

import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_STRUCT
import com.trionesdev.phecda.device.contracts.model.reading.BaseReading.Companion.newBaseReading

open class StructReading : Reading {
    companion object {
        fun newStructReading(
            profileName: String?,
            deviceName: String?,
            resourceName: String?,
            objectValue: Any?
        ): BaseReading {
            return newBaseReading(profileName, deviceName, resourceName, VALUE_TYPE_STRUCT).apply {
                this.objectValue = objectValue
            }
        }
    }

    var objectValue: Any? = null
}