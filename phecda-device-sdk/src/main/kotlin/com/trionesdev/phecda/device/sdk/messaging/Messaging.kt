package com.trionesdev.phecda.device.sdk.messaging

import cn.hutool.core.collection.ListUtil
import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.BootstrapHandlerArgs
import com.trionesdev.phecda.device.sdk.config.ConfigurationStruct

@Slf4j
object Messaging {
    fun messagingBootstrapHandler(args: BootstrapHandlerArgs): Boolean {
        val configuration = args.dic!!.getInstance(ConfigurationStruct::class.java)
        val messagingClient: MessagingClient
        try {
            messagingClient = MessagingFactory.newMessagingClient(configuration!!, args.dic!!)
        } catch (ex: Exception) {
            log.error(
                "Failed to create ChannelClient: {}",
                ex.message,
                ex
            )
            return false
        }
        while (args.startupTimer?.hasNotElapsed() == true) {
            try {
                messagingClient.connect()
            } catch (e: java.lang.Exception) {
                log.warn(
                    "Unable to connect ChannelClient: {}",
                    e.message
                )
                args.startupTimer?.sleepForInterval()
                continue
            }
            args.dic!!.update(ListUtil.toList(messagingClient))
            return true
        }
        log.error("Connecting to ChannelClient time out")
        return true
    }

}