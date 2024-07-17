package com.trionesdev.phecda.device.sdk.messaging.msg

import com.trionesdev.phecda.device.sdk.model.ReplyEvent

class PhecdaReplyEvent: PhecdaEvent() {
    companion object {
        @JvmStatic
        fun newPhecdaReplyEvent(event: ReplyEvent): PhecdaReplyEvent {
            return PhecdaReplyEvent().apply {
                this.version = event.apiVersion
                this.id = event.id
                this.deviceName = event.deviceName
                this.productKey = event.productKey
                this.identifier = event.identifier
                this.ts = event.origin
                this.readings = Reading.fromBaseReadingsToMap(event.readings)
                this.tags = event.tags

                this.replyId = event.replyId
                this.code = event.code
                this.errMsg = event.errMsg
            }
        }
    }

    var replyId: String? = null
    var code: String? = null
    var errMsg: String? = null
}