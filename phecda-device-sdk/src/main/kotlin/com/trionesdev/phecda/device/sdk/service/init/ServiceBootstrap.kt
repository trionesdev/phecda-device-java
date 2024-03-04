package com.trionesdev.phecda.device.sdk.service.init

import com.lmax.disruptor.BlockingWaitStrategy
import com.lmax.disruptor.EventFactory
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.dsl.ProducerType
import com.lmax.disruptor.util.DaemonThreadFactory
import com.trionesdev.kotlin.log.Slf4j
import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.BootstrapHandlerArgs
import com.trionesdev.phecda.device.sdk.cache.Cache
import com.trionesdev.phecda.device.sdk.disruptor.AsyncValuesEvent
import com.trionesdev.phecda.device.sdk.interfaces.AutoEventManager
import com.trionesdev.phecda.device.sdk.service.DeviceDriverService

@Slf4j
class ServiceBootstrap {
    companion object {
        fun newBootstrap(ds: DeviceDriverService): ServiceBootstrap {
            return ServiceBootstrap().apply { this.ds = ds }
        }
    }

    private var ds: DeviceDriverService? = null

    fun bootstrapHandler(args: BootstrapHandlerArgs): Boolean {
        val dic = args.dic!!
        ds!!.apply {
            this.dic = dic
            this.wg = args.wg
            this.autoEventManager = dic.getInstance(AutoEventManager::class.java)
        }
        try {
            Cache.initCache(ds?.serviceKey!!, ds?.baseServiceName!!, dic)
        } catch (e: Exception) {
            log.error("Failed to init cache: {}", e.message, e)
            return false
        }

        if (ds!!.asyncReadingsEnabled()) {
            val asyncCh: Disruptor<AsyncValuesEvent> = Disruptor(
                { AsyncValuesEvent() }, 1024,
                DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, BlockingWaitStrategy()
            )
            ds?.let { it.asyncCh = asyncCh }
            ds?.processAsyncResults(dic)
        }

        //region device driver initialize
        try {
            ds?.driver?.initialize(ds!!)
        } catch (e: Exception) {
            log.error("ProtocolDriver init failed: {}", e.message, e)
            return false
        }
        //endregion

        return true
    }
}