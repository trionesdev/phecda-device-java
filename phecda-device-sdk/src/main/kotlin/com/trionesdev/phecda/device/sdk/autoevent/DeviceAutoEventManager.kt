package com.trionesdev.phecda.device.sdk.autoevent

import com.trionesdev.kotlin.log.Slf4j.Companion.log
import com.trionesdev.phecda.device.bootstrap.BootstrapHandlerArgs
import com.trionesdev.phecda.device.bootstrap.di.Container
import com.trionesdev.phecda.device.contracts.go.WaitGroup
import com.trionesdev.phecda.device.contracts.model.AutoEvent
import com.trionesdev.phecda.device.sdk.cache.Cache
import com.trionesdev.phecda.device.sdk.interfaces.AutoEventManager

class DeviceAutoEventManager : AutoEventManager {
    companion object {
        fun bootstrapHandler(args: BootstrapHandlerArgs): Boolean {
            val manager = DeviceAutoEventManager().apply {
                wg = args.wg
                dic = args.dic
                executorMap = mutableMapOf()
            }
            args.dic?.update(listOf(manager))
            return true
        }
    }

    private var executorMap = mutableMapOf<String, MutableList<Executor>>()
    private var wg: WaitGroup? = null
    private var dic: Container? = null

    fun triggerExecutors(deviceName: String, autoEvents: List<AutoEvent>?, dic: Container): MutableList<Executor> {
        val executors = mutableListOf<Executor>()
        if (!autoEvents.isNullOrEmpty()){
            for (autoEvent in autoEvents) {
                try {
                    Executor.newExecutor(deviceName, autoEvent).let {
                        executors.add(it)
                        it.run(wg!!, dic)
                    }
                } catch (e: Exception) {
                    log.error(
                        "failed to create executor of AutoEvent {} for Device {}: {}",
                        autoEvent.identifier,
                        deviceName,
                        e.message,
                        e
                    )
                    continue
                }
            }
        }
        return executors
    }

    override fun startAutoEvents() {
        val devices = Cache.devices()?.all()
        devices?.forEach { device ->
            device?.let {
                if (!executorMap.containsKey(device.name)) {
                    executorMap[device.name!!] = triggerExecutors(device.name!!, device.autoEvents, dic!!)
                }
            }
        }
    }

    override fun restartForDevice(name: String?) {
        stopForDevice(name!!)
        Cache.devices()?.forName(name)?.let {
            triggerExecutors(it.name!!, it.autoEvents!!, dic!!).let { executors ->
                executorMap[it.name!!] = executors
            }
        } ?: let {
            log.error(
                "failed to find device {} in cache to start AutoEvent",
                name
            )
        }
    }

    override fun stopForDevice(deviceName: String?) {
        executorMap[deviceName]?.let {
            for (executor in it) {
                executor.stop()
            }
            executorMap.remove(deviceName)
        }
    }
}