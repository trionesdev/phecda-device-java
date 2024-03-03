package com.trionesdev.phecda.device.bootstrap

import com.trionesdev.phecda.device.bootstrap.args.CommonArgs
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.bootstrap.environement.Variables
import com.trionesdev.phecda.device.bootstrap.interfaces.Configuration
import com.trionesdev.phecda.device.bootstrap.startup.Timer
import com.trionesdev.phecda.device.contracts.go.WaitGroup

object Bootstrap {

    fun runAndReturnWaitGroup(
        args: CommonArgs?,
        serviceKey:String?,
        serviceConfig:Configuration?,
        startupTimer:Timer?,
        dic:Container?,
        bootstrapHandlers: MutableList<Function1<BootstrapHandlerArgs,Boolean>>?
    ):WaitGroup{
        var wg = WaitGroup()
        val envVars = Variables.newVariables();
        return wg
    }

}