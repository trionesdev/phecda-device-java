package com.trionesdev.phecda.device.bootstrap

import com.trionesdev.phecda.device.bootstrap.args.CommonArgs
import com.trionesdev.phecda.device.bootstrap.config.Processor
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.bootstrap.environement.Variables
import com.trionesdev.phecda.device.bootstrap.interfaces.Configuration
import com.trionesdev.phecda.device.bootstrap.startup.Timer
import com.trionesdev.phecda.device.contracts.go.WaitGroup

object Bootstrap {

    fun runAndReturnWaitGroup(
        args: CommonArgs?,
        serviceKey: String?,
        serviceConfig: Configuration?,
        startupTimer: Timer?,
        dic: Container?,
        bootstrapHandlers: MutableList<Function1<BootstrapHandlerArgs, Boolean>>?
    ): WaitGroup {
        val wg = WaitGroup()
        val envVars = Variables.newVariables()
        Processor.newProcessor(args, envVars, startupTimer, dic!!).let {
            it.process(serviceKey, serviceConfig)
        }
        dic.update(mutableListOf(serviceConfig))
        if (bootstrapHandlers.isNullOrEmpty()) {
            return wg
        }
        for (handler in bootstrapHandlers) {
            if (!handler.invoke(BootstrapHandlerArgs().apply {
                    this.wg = wg
                    this.dic = dic
                    this.startupTimer = startupTimer
                })) {
                break
            }
        }
        return wg
    }

}