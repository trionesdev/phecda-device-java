package com.trionesdev.phecda.device.contracts.model

class DeviceProfile:DBTimestamp() {

//     var id: String? = null
//     var name: String? = null
//     var description: String? = null
//     var manufacturer: String? = null
//     var model: String? = null
//     var labels: MutableList<String>? = null
//     var deviceResources: MutableList<DeviceResource>? = null
//     var deviceCommands: MutableList<DeviceCommand>? = null


     var productKey: String? = null
     var name: String? = null
     var description: String? = null
     var manufacturer: String? = null
     var labels: MutableList<String>? = null
     var deviceProperties: MutableList<DeviceProperty>? = null
     var deviceCommands: MutableList<DeviceCommand>? = null
     var deviceEvents: MutableList<DeviceEvent>? = null
}