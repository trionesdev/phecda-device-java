package com.trionesdev.phecda.device.bootstrap.di

import org.glassfish.hk2.api.ServiceLocator
import org.glassfish.hk2.utilities.ServiceLocatorUtilities
import org.glassfish.hk2.utilities.binding.AbstractBinder

class Container {
    companion object {
        @JvmStatic
        val locator: ServiceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator()
        fun newContainer(instances: List<Any?>): Container {
            val container = Container()
            container.update(instances)
            return container
        }
    }

    fun update(instances: List<Any?>) {
        if (instances.isNotEmpty()) {
            instances.forEach { instance ->
                val binder: AbstractBinder = object : AbstractBinder() {
                    override fun configure() {
                        instance ?: let { return }
                        this.bind(instance).to(instance.javaClass)
                        val interfaces = instance.javaClass.interfaces
                        if (interfaces.isNotEmpty()) {
                            for (i in interfaces) {
                                bind(instance).to(i as Class<in Any>)
                            }
                        }
                    }
                }
                ServiceLocatorUtilities.bind(locator, binder)
            }
        }
    }

    fun update(instances: Map<Class<*>, Any?>) {
        if (instances.isNotEmpty()) {
            instances.forEach { (key, value) ->
                ServiceLocatorUtilities.bind(locator, object : AbstractBinder() {
                    override fun configure() {
                        value?.let {
                            bind(value).to(key as Class<in Any>)
                        }
                    }
                })
            }
        }
    }

    fun <T> getInstance(clazz: Class<T>?): T? {
        return clazz?.let {
            return locator.getService<T>(clazz)
        }
    }

}