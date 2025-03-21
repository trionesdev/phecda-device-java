package com.trionesdev.phecda.device.sdk.model

import com.trionesdev.phecda.device.contracts.model.Event

class ReplyEvent : Event() {
    var replyId: String? = null
    var code: String? = null
    var errMsg: String? = null

    fun applyEvent(event: Event?) {
        this.code = "ok"
        event?.let {
            this.id = event.id
            this.deviceName = event.deviceName
            this.productKey = event.productKey
            this.identifier = event.identifier
            this.origin = event.origin
            this.readings = event.readings
            this.tags = event.tags
            this.readings = event.readings
            this.tags = event.tags
        }
    }

}