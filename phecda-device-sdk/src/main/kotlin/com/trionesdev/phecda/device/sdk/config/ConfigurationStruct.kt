package com.trionesdev.phecda.device.sdk.config

import com.alibaba.fastjson2.annotation.JSONField
import com.trionesdev.phecda.device.bootstrap.interfaces.Configuration

class ConfigurationStruct : Configuration {
    @JSONField(name = "MaxEventSize")
    var maxEventSize: Long = 0

    @JSONField(name = "Device")
    var device: DeviceInfo? = null

    @JSONField(name = "Driver")
    var driver: Map<String, String>? = null

}