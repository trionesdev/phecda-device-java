package com.trionesdev.phecda.device.sdk.messaging.mqtt

import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttCallback(mqttClient: MqttMessagingClient) : MqttCallbackExtended {
    var mqttClient: MqttMessagingClient? = mqttClient

    override fun connectionLost(cause: Throwable?) {
        mqttClient?.dic?.getInstance(ProtocolDriver::class.java)?.let { protocolDriver ->
            protocolDriver.messagingConnectionLost(cause)
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        TODO("Not yet implemented")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        TODO("Not yet implemented")
    }

    override fun connectComplete(reconnect: Boolean, serverURI: String?) {

    }
}