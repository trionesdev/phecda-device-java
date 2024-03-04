package com.trionesdev.phecda.device.sdk.interfaces

interface ProtocolDriver {
    fun initialize(sdk: DeviceDriverServiceSDK)
    fun start()
}