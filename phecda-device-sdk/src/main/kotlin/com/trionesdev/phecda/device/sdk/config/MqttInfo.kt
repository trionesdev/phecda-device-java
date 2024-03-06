package com.trionesdev.phecda.device.sdk.config

import com.alibaba.fastjson2.annotation.JSONField

class MqttInfo {
    @JSONField(name = "ClientId")
    var clientId: String? = null

    @JSONField(name = "Protocol")
    var protocol: String? = null

    @JSONField(name = "Host")
    var host: String? = null

    @JSONField(name = "Port")
    var port = 0

    @JSONField(name = "Username")
    var username: String? = null

    @JSONField(name = "Password")
    var password: String? = null

    @JSONField(name = "QOS")
    var qos = 0


    @JSONField(name = "ConnectionTimeout")
    var connectionTimeout = 0

    @JSONField(name = "KeepAliveInterval")
    var keepAliveInterval = 0

    @JSONField(name = "CleanSession")
    var cleanSession = true

    @JSONField(name = "AutomaticReconnect")
    var automaticReconnect: Boolean = true

    @JSONField(name = "TopicPrefix")
    var topicPrefix = "phecda"
}