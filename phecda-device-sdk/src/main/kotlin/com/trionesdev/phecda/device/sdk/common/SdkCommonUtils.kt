package com.trionesdev.phecda.device.sdk.common

import com.alibaba.fastjson2.JSON
import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.model.Event
import com.trionesdev.phecda.device.contracts.model.reading.BaseReading
import com.trionesdev.phecda.device.sdk.cache.Cache
import com.trionesdev.phecda.device.sdk.config.ConfigurationStruct
import kotlin.math.log

@Slf4j
object SdkCommonUtils {

    /**
     * send event
     * this is a method send event to things platform
     */
    fun sendEvent(event: Event, correlationID: String?, dic: Container) {
        val configuration = dic.getInstance(ConfigurationStruct::class.java)
        log.debug(JSON.toJSONString(event))
    }

    fun addEventTags(event: Event) {
        event.tags ?: let { event.tags = mutableMapOf() }
        val cmd = Cache.profiles()?.deviceCommand(event.profileName, event.sourceName)
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

    fun addReadingTags(reading: BaseReading?) {
        val dr = Cache.profiles()?.deviceResource(reading?.profileName!!, reading.resourceName!!)
        dr?.let {
            if (dr.tags.isNullOrEmpty()) {
                if (reading?.tags?.isEmpty() == true) {
                    reading.tags = dr.tags
                } else {
                    dr.tags?.let { it1 -> reading?.tags?.putAll(it1) }
                }
            }
        }

    }

}