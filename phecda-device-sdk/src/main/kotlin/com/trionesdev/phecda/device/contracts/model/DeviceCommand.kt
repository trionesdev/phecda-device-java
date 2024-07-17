package com.trionesdev.phecda.device.contracts.model

class DeviceCommand {
//    var name: String? = null
//    var isHidden = false
//    var readWrite: String? = null
//    var resourceOperations: MutableList<ResourceOperation>? = null
//    var tags: MutableMap<String, Any>? = null

    var identifier: String? = null
    var name:String? = null
    var description:String? = null
    var readWrite: String? = null
    var type:String? = null
    var inputData: MutableList<InputItem>? = null
    var outputData: MutableList<OutputItem>? = null
    var tags: MutableMap<String, String>? = null

    class InputItem {
        var identifier:String? = null
        var name:String? = null
        var attributes: MutableMap<String, Any?>? = null
        var properties: ResourceProperties? = null
    }

    class OutputItem {
        var identifier:String? = null
        var name:String? = null
        var attributes: MutableMap<String, Any?>? = null
        var properties: ResourceProperties? = null
    }

}