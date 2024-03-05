package com.trionesdev.phecda.device.contracts.model

import com.trionesdev.phecda.device.contracts.model.enums.AdminState

class DeviceService : DBTimestamp() {
    var id: String? = null
    var name: String? = null
    val description: String? = null
    val labels: MutableList<String>? = null
    var adminState: AdminState? = null
}