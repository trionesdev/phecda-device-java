package com.trionesdev.phecda.device.sdk.service.init

import com.trionesdev.phecda.device.bootstrap.BootstrapHandlerArgs
import com.trionesdev.phecda.device.sdk.messaging.Messaging

class MessagingBootstrap {
    companion object {
        fun newMessagingBootstrap(baseServiceName: String?): MessagingBootstrap {
            return MessagingBootstrap().apply {
                this.baseServiceName = baseServiceName
            }
        }
    }

    var baseServiceName: String? = null

    fun bootstrapHandler(args: BootstrapHandlerArgs): Boolean {
        if (!Messaging.messagingBootstrapHandler(args)) {
            return false;
        }
        return true
    }

}