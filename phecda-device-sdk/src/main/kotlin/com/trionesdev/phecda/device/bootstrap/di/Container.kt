package com.trionesdev.phecda.device.bootstrap.di

import org.glassfish.hk2.api.ServiceLocator
import org.glassfish.hk2.utilities.ServiceLocatorUtilities
import org.glassfish.hk2.utilities.binding.AbstractBinder
import java.util.*

class Container {
    companion object {
        @JvmStatic
        val locator: ServiceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator()
        fun newContainer(instances: List<Any?>): Container {
            val container = Container()
            container.init()
            container.update(instances)
            return container
        }
    }

    fun update(instances: List<Any?>) {
        if (instances.isNotEmpty()) {
            instances.forEach { instance ->
                ServiceLocatorUtilities.bind(locator, object : AbstractBinder() {
                    override fun configure() {
                        instance ?: let { return }
                        bind(instance) to instance.javaClass as Class<*>
                        val interfaces = instance.javaClass.interfaces
                        if (interfaces.isNotEmpty()) {
                            Arrays.stream(interfaces).forEach { ic: Class<*>? ->
                                bind(instance) to ic
                            }
                        }
                    }
                })
            }
        }
    }

    fun update(instances: Map<Class<*>, Any>) {
        if (instances.isNotEmpty()) {
            instances.forEach { (key, value) ->
                ServiceLocatorUtilities.bind(locator, object : AbstractBinder() {
                    override fun configure() {
                        if (Objects.isNull(value)) {
                            return
                        }
                        bind(value) to key
                    }
                })
            }
        }
    }

    fun <T> getInstance(clazz: Class<T>?): T? {
        if (Objects.isNull(clazz)) {
            return null
        }
        return locator.getService(clazz)
    }


    fun init() {}

}