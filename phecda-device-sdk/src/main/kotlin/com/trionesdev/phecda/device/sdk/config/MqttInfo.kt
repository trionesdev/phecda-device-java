package com.trionesdev.phecda.device.sdk.config

import com.google.gson.annotations.SerializedName

//import com.alibaba.fastjson2.annotation.JSONField

class MqttInfo {
//    @JSONField(name = "ClientId")
    @SerializedName("ClientId")
    var clientId: String? = null

//    @JSONField(name = "Protocol")
    @SerializedName("Protocol")
    var protocol: String? = null

//    @JSONField(name = "Host")
    @SerializedName("Host")
    var host: String? = null

//    @JSONField(name = "Port")
    @SerializedName("Port")
    var port = 0

//    @JSONField(name = "Username")
    @SerializedName("Username")
    var username: String? = null

//    @JSONField(name = "Password")
    @SerializedName("Password")
    var password: String? = null

//    @JSONField(name = "QOS")
    @SerializedName("QOS")
    var qos = 0


//    @JSONField(name = "ConnectionTimeout")
    @SerializedName("ConnectionTimeout")
    var connectionTimeout = 0

//    @JSONField(name = "KeepAliveInterval")
    @SerializedName("KeepAliveInterval")
    var keepAliveInterval = 0

//    @JSONField(name = "CleanSession")
    @SerializedName("CleanSession")
    var cleanSession = true

//    @JSONField(name = "AutomaticReconnect")
    @SerializedName("AutomaticReconnect")
    var automaticReconnect: Boolean = true

//    @JSONField(name = "TopicPrefix")
    @SerializedName("TopicPrefix")
    var topicPrefix = "phecda"
}