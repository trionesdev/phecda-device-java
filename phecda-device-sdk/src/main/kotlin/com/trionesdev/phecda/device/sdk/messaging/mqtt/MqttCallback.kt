package com.trionesdev.phecda.device.sdk.messaging.mqtt

import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.sdk.interfaces.ProtocolDriver
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage

@Slf4j
class MqttCallback(mqttClient: MqttMessagingClient) : MqttCallbackExtended {
    var mqttClient: MqttMessagingClient? = mqttClient

    override fun connectionLost(cause: Throwable?) {
        log.error("MQTT connection lost: {}", cause?.message)
        mqttClient?.dic?.getInstance(ProtocolDriver::class.java)?.let { protocolDriver ->
            protocolDriver.messagingConnectionLost(cause)
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {

    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {

    }

    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
        mqttClient?.subscribeDefault()
    }
}