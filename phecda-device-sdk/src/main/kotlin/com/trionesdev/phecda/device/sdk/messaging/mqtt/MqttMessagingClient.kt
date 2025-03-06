package com.trionesdev.phecda.device.sdk.messaging.mqtt

import com.alibaba.fastjson2.JSON
import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.common.CommonConstants
import com.trionesdev.phecda.device.contracts.errors.CommonPhecdaException
import com.trionesdev.phecda.device.sdk.application.ApplicationCommand
import com.trionesdev.phecda.device.sdk.cache.Cache
import com.trionesdev.phecda.device.sdk.config.MqttInfo
import com.trionesdev.phecda.device.sdk.messaging.MessagingClient
import com.trionesdev.phecda.device.sdk.messaging.msg.PhecdaCommand
import com.trionesdev.phecda.device.sdk.messaging.msg.PhecdaReplyEvent
import com.trionesdev.phecda.device.sdk.model.ReplyEvent
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.time.Instant

@Slf4j
class MqttMessagingClient : MessagingClient {
    companion object {
        fun newMqttClient(mqttInfo: MqttInfo, dic: Container): MqttMessagingClient {
            val serverUri = (mqttInfo.protocol + "://" + mqttInfo.host) + ":" + mqttInfo.port
            val persistence = MemoryPersistence()
            var mqttClient: IMqttAsyncClient? = null
            try {
                mqttClient = MqttAsyncClient(serverUri, mqttInfo.clientId, persistence)
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

    var dic: Container? = null
    private var mqttClient: IMqttAsyncClient? = null
    private var options: MqttConnectOptions? = null
    private var topicPrefix = "phecda"
    override fun connect() {
        try {
            mqttClient!!.setCallback(MqttCallback(this))
            mqttClient!!.connect(options)
        } catch (e: Exception) {
            throw CommonPhecdaException(e.message, e)
        }
    }

    override fun publish(topic: String?, message: ByteArray?) {
        val mqttMessage = MqttMessage(message)
        try {
            mqttClient!!.publish("$topicPrefix/$topic", mqttMessage)
        } catch (e: MqttException) {
            log.error(e.message, e)
            throw RuntimeException(e)
        }
    }

    override fun subscribeDefault() {
        Cache.profiles()?.all()?.let { profiles ->
            for (profile in profiles) {
                mqttClient?.subscribe(
                    "$topicPrefix/${profile.productKey}/+/thing/command/+",
                    0
                ) { topic: String?, message: MqttMessage ->
                    val command = JSON.parseObject(
                        message.payload,
                        PhecdaCommand::class.java
                    )
                    val queryParams = command.inputData?.map { "${it.key}=${it.value}" }?.joinToString("&")
                    val syncReplayTopic = "$topicPrefix/thing/command/${command.id}/reply/sync"
                    val asyncReplayTopic = "$topicPrefix/thing/command/${command.id}/reply/async"
                    val replyEvent: ReplyEvent = ReplyEvent().apply {
                        this.id = command.id
                        this.replyId = command.id
                        this.deviceName = command.deviceName
                        this.productKey = command.productKey
                        this.identifier = command.identifier
                        this.origin = Instant.now().toEpochMilli()
                    }

                    val deviceCommand = Cache.profiles()?.deviceCommand(profile.productKey, command.identifier)
                    if (deviceCommand == null) {
                        replyEvent.apply {
                            code = "1"
                            errMsg = "command not found"
                        }
                    } else {
                        val method = if (deviceCommand.readWrite == CommonConstants.READ_WRITE_R) "get" else "set"
                        if (method == "get") {
                            try {
                                val evt = ApplicationCommand.getCommand(
                                    command.deviceName,
                                    command.identifier,
                                    queryParams,
                                    dic!!
                                )
                                replyEvent.applyEvent(evt)
                            } catch (e: Exception) {
                                replyEvent.apply {
                                    errMsg = e.message
                                }
                            }
                        } else {
                            try {
                                val evt = ApplicationCommand.setCommand(
                                    command.deviceName,
                                    command.identifier,
                                    queryParams,
                                    command.inputData,
                                    dic!!
                                )
                                replyEvent.applyEvent(evt)
                            } catch (e: Exception) {
                                replyEvent.apply {
                                    errMsg = e.message
                                }
                            }
                        }
                    }

                    val payload = PhecdaReplyEvent.newPhecdaReplyEvent(replyEvent)
                    if (command.sync == true) {
                        mqttClient?.publish(syncReplayTopic, MqttMessage(JSON.toJSONBytes(payload)))
                    } else {
                        mqttClient?.publish(asyncReplayTopic, MqttMessage(JSON.toJSONBytes(payload)))
                    }
                }
            }
        }
    }

    override fun subscribe(topic: String?, callback: (String?, ByteArray?) -> Unit?) {
        mqttClient?.subscribe("$topicPrefix/$topic", 0) { topic, message -> callback(topic, message.payload) }
    }

}