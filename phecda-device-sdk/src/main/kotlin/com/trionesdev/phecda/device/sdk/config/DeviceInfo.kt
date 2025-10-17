package com.trionesdev.phecda.device.sdk.config

//import com.alibaba.fastjson2.annotation.JSONField
import com.google.gson.annotations.SerializedName

class DeviceInfo {
//    @JSONField(name = "DataTransform")
    @SerializedName("DataTransform")
    var dataTransform = false

//    @JSONField(name = "MaxCmdOps")
    @SerializedName("MaxCmdOps")
    var maxCmdOps = 0

//    @JSONField(name = "ProfilesDir")
    @SerializedName("ProfilesDir")
    var profilesDir: String? = null

//    @JSONField(name = "DevicesDir")
    @SerializedName("DevicesDir")
    var devicesDir: String? = null

//    @JSONField(name = "EnableAsyncReadings")
    @SerializedName("EnableAsyncReadings")
    var enableAsyncReadings = false
}