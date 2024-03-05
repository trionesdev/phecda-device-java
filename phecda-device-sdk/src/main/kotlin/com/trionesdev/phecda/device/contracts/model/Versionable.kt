package com.trionesdev.phecda.device.contracts.model

import com.trionesdev.phecda.device.contracts.common.CommonConstants.API_VERSION

open class Versionable {
    companion object {
        fun newVersionable(): Versionable {
            return Versionable().apply {
                this.apiVersion = API_VERSION
            }
        }
    }

    var apiVersion: String? = null
}