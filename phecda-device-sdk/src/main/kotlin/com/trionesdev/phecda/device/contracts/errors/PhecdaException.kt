package com.trionesdev.phecda.device.contracts.errors

interface PhecdaException {
    fun error(): String
    fun debugMessages():String
    fun message():String
    fun code():Int
}