package com.trionesdev.phecda.device.contracts.model

class DeviceProfile:DBTimestamp() {
     val description: String? = null
     val id: String? = null
     val name: String? = null
     val manufacturer: String? = null
     val model: String? = null
     val labels: List<String>? = null
     val deviceResources: List<DeviceResource>? = null
     val deviceCommands: List<DeviceCommand>? = null
}