package com.trionesdev.phecda.device.sdk.config

import com.alibaba.fastjson2.annotation.JSONField

class DeviceInfo {
    @JSONField(name = "ProfilesDir")
    var profilesDir: String? = null

    @JSONField(name = "DevicesDir")
    var devicesDir: String? = null

    @JSONField(name = "EnableAsyncReadings")
    var enableAsyncReadings = false
}