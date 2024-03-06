package com.trionesdev.phecda.device.sdk.messaging

interface MessagingClient {
    fun connect()
    fun publish(topic: String?, message: ByteArray?)
}