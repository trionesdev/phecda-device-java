package com.trionesdev.phecda.device.contracts.errors

class CommonPhedaException : RuntimeException(), PhecdaException {

    companion object {
        fun newCommonPhedaException(kind: String?, message: String?, wreppedError: Exception?): CommonPhedaException {
            return CommonPhedaException()
        }
    }

    var callerInfo: String? = null
    var kind: String? = null
    var code: Int? = null
    var err: Exception? = null

    override fun error(): String {
        TODO("Not yet implemented")
    }

    override fun debugMessages(): String {
        TODO("Not yet implemented")
    }

    override fun message(): String {
        TODO("Not yet implemented")
    }

    override fun code(): Int {
        TODO("Not yet implemented")
    }


}