package com.trionesdev.phecda.device.sdk.interfaces

interface AutoEventManager {
    fun startAutoEvents()
    fun restartForDevice(name: String?)
    fun stopForDevice(deviceName: String?)
}