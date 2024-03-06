package com.trionesdev.phecda.device.sdk.messaging.mqtt

import com.alibaba.fastjson2.JSON
import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.errors.CommonPhedaException
import com.trionesdev.phecda.device.sdk.application.ApplicationCommand
import com.trionesdev.phecda.device.sdk.cache.Cache
import com.trionesdev.phecda.device.sdk.config.MqttInfo
import com.trionesdev.phecda.device.sdk.messaging.MessagingClient
import com.trionesdev.phecda.device.sdk.messaging.msg.PhecdaCommand
import org.eclipse.paho.client.mqttv3.*
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

    override fun subscribe(topic: String?, callback: (String?, ByteArray?) -> Unit?) {
        mqttClient?.subscribe("$topicPrefix/$topic", 0) { topic, message -> callback(topic, message.payload) }
    }

    fun subscribe() {
        Cache.profiles()?.all()?.let { profiles ->
            for (profile in profiles) {
                mqttClient?.subscribe("$topicPrefix/${profile.name}/+/thing/service") { topic: String?, message: MqttMessage ->
                    val command = JSON.parseObject(
                        message.payload,
                        PhecdaCommand::class.java
                    )
                    ApplicationCommand.setCommand(command.deviceName, command.commandName, "", command.params, dic!!)
                }
            }
        }
    }
}