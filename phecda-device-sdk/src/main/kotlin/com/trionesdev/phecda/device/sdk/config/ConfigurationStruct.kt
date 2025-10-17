package com.trionesdev.phecda.device.sdk.config

//import com.alibaba.fastjson2.annotation.JSONField
import com.google.gson.annotations.SerializedName
import com.trionesdev.phecda.device.bootstrap.interfaces.Configuration

class ConfigurationStruct : Configuration {
//    @JSONField(name = "MaxEventSize")
    @SerializedName("MaxEventSize")
    var maxEventSize: Long = 0

//    @JSONField(name = "Device")
    @SerializedName("Device")
    var device: DeviceInfo? = null

//    @JSONField(name = "Driver")
    @SerializedName("Driver")
    var driver: Map<String, String>? = null

//    @JSONField(name = "MQTT")
    @SerializedName("MQTT")
    var mqtt: MqttInfo? = null
}