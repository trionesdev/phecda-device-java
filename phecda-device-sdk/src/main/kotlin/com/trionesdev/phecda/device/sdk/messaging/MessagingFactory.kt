package com.trionesdev.phecda.device.sdk.messaging

import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.sdk.config.ConfigurationStruct
import com.trionesdev.phecda.device.sdk.messaging.mqtt.MqttMessagingClient

object MessagingFactory {
    fun newMessagingClient(configuration: ConfigurationStruct, dic: Container): MessagingClient {
        return MqttMessagingClient.newMqttClient(configuration.mqtt!!, dic)
    }
}