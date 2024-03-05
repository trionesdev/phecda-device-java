package com.trionesdev.phecda.device.sdk.application

import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.model.Event


@Slf4j
object ApplicationCommand {
    fun getCommand(
        deviceName: String,
        commandName: String,
        queryParams: String,
        regexCmd: Boolean,
        dic: Container
    ): Event? {
        log.info("getCommand deviceName: $deviceName, commandName: $commandName, queryParams: $queryParams, regexCmd: $regexCmd")
        return null
    }
}