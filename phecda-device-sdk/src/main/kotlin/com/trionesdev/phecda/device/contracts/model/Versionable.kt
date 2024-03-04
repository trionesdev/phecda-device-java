package com.trionesdev.phecda.device.contracts.model

import com.trionesdev.phecda.device.contracts.common.CommonConstants.ApiVersion

open class Versionable {
    companion object {
        fun newVersionable(): Versionable {
            return Versionable().apply {
                this.apiVersion = ApiVersion
            }
        }
    }

    var apiVersion: String? = null
}