package com.trionesdev.phecda.device.contracts.model.reading

import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_OBJECT
import com.trionesdev.phecda.device.contracts.model.reading.BaseReading.Companion.newBaseReading

class ObjectReading : Reading {
    companion object {
        fun newObjectReading(
            profileName: String?,
            deviceName: String?,
            resourceName: String?,
            objectValue: Any?
        ): BaseReading {
            return newBaseReading(profileName, deviceName, resourceName, VALUE_TYPE_OBJECT).apply {
                this.objectValue = objectValue
            }
        }
    }

    var objectValue: Any? = null
}