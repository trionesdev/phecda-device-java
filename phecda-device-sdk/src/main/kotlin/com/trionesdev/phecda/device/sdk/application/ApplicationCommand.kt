package com.trionesdev.phecda.device.sdk.application

import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.model.Event

object ApplicationCommand {
    fun getCommand(deviceName: String, commandName: String, queryParams: String, regexCmd: Boolean, dic: Container):Event? {
        return null
    }
}