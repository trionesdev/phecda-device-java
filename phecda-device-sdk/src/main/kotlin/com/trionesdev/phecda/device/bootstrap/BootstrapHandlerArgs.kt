package com.trionesdev.phecda.device.bootstrap

import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.bootstrap.startup.Timer
import com.trionesdev.phecda.device.contracts.go.WaitGroup

class BootstrapHandlerArgs {
    var wg: WaitGroup? = null
    var startupTimer: Timer? = null
    var dic: Container? = null
}