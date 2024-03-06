package com.trionesdev.phecda.device.sdk.messaging.mqtt

import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.errors.CommonPhedaException
import com.trionesdev.phecda.device.sdk.config.MqttInfo
import com.trionesdev.phecda.device.sdk.messaging.MessagingClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

@Slf4j
class MqttMessagingClient : MessagingClient {
    companion object {
        fun newMqttClient(mqttInfo: MqttInfo, dic: Container): MqttMessagingClient {
            val serverUri = (mqttInfo.protocol + "://" + mqttInfo.host) + ":" + mqttInfo.port
            val persistence = MemoryPersistence()
            var mqttClient: MqttClient? = null
            try {
                mqttClient = MqttClient(serverUri, mqttInfo.clientId, persistence)
            } catch (e: MqttException) {
                log.error(e.message, e)
                throw RuntimeException(e)
            }
            val connOpts = MqttConnectOptions().apply {
                mqttInfo.username?.let { username -> this.userName = username }
                mqttInfo.password?.let { password -> this.password = password.toCharArray() }
                if (mqttInfo.connectionTimeout > 0) {
                    this.connectionTimeout = mqttInfo.connectionTimeout
                }
                mqttInfo.cleanSession.let { this.isCleanSession = it }
                if (mqttInfo.keepAliveInterval > 0) {
                    this.keepAliveInterval = mqttInfo.keepAliveInterval
                }
                mqttInfo.automaticReconnect.let { this.isAutomaticReconnect = it }
            }
            return MqttMessagingClient().apply {
                this.dic = dic
                this.mqttClient = mqttClient
                this.options = connOpts
                this.topicPrefix = mqttInfo.topicPrefix
            }
        }
    }

    private var dic: Container? = null
    private var mqttClient: MqttClient? = null
    private var options: MqttConnectOptions? = null
    private var topicPrefix = "phecda"
    override fun connect() {
        try {
            mqttClient!!.setCallback(MqttCallback(this))
            mqttClient!!.connect(options)
        } catch (e: Exception) {
            throw CommonPhedaException(e.message, e)
        }
    }

    override fun publish(topic: String?, message: ByteArray?) {
        val mqttMessage = MqttMessage(message)
        try {
            mqttClient!!.publish("$topicPrefix/$topic", mqttMessage)
        } catch (e: MqttException) {
            log.error(e.message, e)
            throw java.lang.RuntimeException(e)
        }
    }

    fun subscribe() {}
}