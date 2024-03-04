package com.trionesdev.phecda.device.contracts.model

class DeviceProfile:DBTimestamp() {

     val id: String? = null
     val name: String? = null
     val description: String? = null
     val manufacturer: String? = null
     val model: String? = null
     val labels: MutableList<String>? = null
     val deviceResources: MutableList<DeviceResource>? = null
     val deviceCommands: MutableList<DeviceCommand>? = null
}