package com.trionesdev.phecda.device.contracts.model.reading

import com.trionesdev.phecda.device.contracts.common.CommonConstants.ValueTypeStruct
import com.trionesdev.phecda.device.contracts.model.reading.BaseReading.Companion.newBaseReading

open class StructReading : Reading {
    companion object {
        fun newStructReading(
            profileName: String?,
            deviceName: String?,
            resourceName: String?,
            objectValue: Any?
        ): BaseReading {
            return newBaseReading(profileName, deviceName, resourceName, ValueTypeStruct).apply {
                this.objectValue = objectValue
            }
        }
    }

    var objectValue: Any? = null
}