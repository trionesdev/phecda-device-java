package com.trionesdev.phecda.device.contracts.model.reading

import com.trionesdev.phecda.device.contracts.common.CommonConstants.VALUE_TYPE_BINARY
import com.trionesdev.phecda.device.contracts.model.reading.BaseReading.Companion.newBaseReading

open class BinaryReading : Reading {
    companion object {
        fun newBinaryReading(
            profileName: String?,
            deviceName: String?,
            resourceName: String?,
            binaryValue: ByteArray?,
            mediaType: String?
        ): BaseReading {
            return newBaseReading(profileName, deviceName, resourceName, VALUE_TYPE_BINARY).apply {
                this.binaryValue = binaryValue
                this.mediaType = mediaType
            }
        }
    }

    var binaryValue: ByteArray? = null
    var mediaType: String? = null
}