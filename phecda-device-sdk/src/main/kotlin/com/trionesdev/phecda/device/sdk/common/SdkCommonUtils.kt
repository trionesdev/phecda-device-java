package com.trionesdev.phecda.device.sdk.common

import com.alibaba.fastjson2.JSON
import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.model.Event
import com.trionesdev.phecda.device.contracts.model.reading.BaseReading
import com.trionesdev.phecda.device.sdk.cache.Cache
import com.trionesdev.phecda.device.sdk.messaging.MessagingClient
import com.trionesdev.phecda.device.sdk.messaging.msg.PhecdaEvent

@Slf4j
object SdkCommonUtils {

    /**
     * send event
     * this is a method send event to things platform
     */
    fun sendEvent(event: Event, correlationID: String?, dic: Container) {
        log.info(JSON.toJSONString(event))
        dic.getInstance(MessagingClient::class.java)?.let { client ->
            val phecdaEvent = PhecdaEvent.newPhecdaEvent(event)
            log.info(JSON.toJSONString(phecdaEvent))
            if (event.tags?.containsKey("event") == true) {
                client.publish(
                    "${event.productKey}/${event.deviceName}/thing/event/post",
                    JSON.toJSONBytes(phecdaEvent)
                )
            } else {
                client.publish(
                    "${event.productKey}/${event.deviceName}/thing/property/post",
                    JSON.toJSONBytes(phecdaEvent)
                )
            }
        }
    }

    fun addEventTags(event: Event) {
        event.tags ?: let { event.tags = mutableMapOf() }
        val cmd = Cache.profiles()?.deviceCommand(event.productKey, event.identifier)
        cmd?.let {
            if (!cmd.tags.isNullOrEmpty()) {
                event.tags?.putAll(cmd.tags!!)
            }
        }
        val device = Cache.devices()?.forName(event.deviceName!!)
        device?.let {
            if (!device.tags.isNullOrEmpty()) {
                event.tags?.putAll(device.tags!!)
            }
        }
    }

    fun addReadingTags(reading: BaseReading) {
        val dr = Cache.profiles()?.deviceProperty(reading.productKey!!, reading.identifier!!)
        dr?.let {
            if (!dr.tags.isNullOrEmpty()) {
                if (reading.tags.isNullOrEmpty()) {
                    reading.tags = mutableMapOf()
                }
                reading.tags?.putAll(dr.tags!!)
            }
        }

    }

}