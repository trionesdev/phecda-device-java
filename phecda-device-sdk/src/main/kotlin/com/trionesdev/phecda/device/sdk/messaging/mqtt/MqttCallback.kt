package com.trionesdev.phecda.device.sdk.messaging.mqtt

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttCallback(mqttClient: MqttMessagingClient) : MqttCallbackExtended {
    var mqttClient: MqttMessagingClient? = mqttClient

    override fun connectionLost(cause: Throwable?) {
        TODO("Not yet implemented")
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        TODO("Not yet implemented")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        TODO("Not yet implemented")
    }

    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
        try {
            mqttClient?.subscribe()
        } catch (e: MqttException) {
            throw RuntimeException(e)
        }
    }
}