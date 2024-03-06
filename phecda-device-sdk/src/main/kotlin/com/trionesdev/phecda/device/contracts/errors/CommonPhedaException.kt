package com.trionesdev.phecda.device.contracts.errors

class CommonPhedaException() : RuntimeException(), PhecdaException {

    companion object {
        fun newCommonPhedaException(kind: String?, message: String?, wreppedError: Exception?): CommonPhedaException {
            return CommonPhedaException().apply {
                this.kind = kind
                this.message = message
                this.err = wreppedError
            }
        }
    }

    var callerInfo: String? = null
    var kind: String? = null
    override var message: String? = null
    var code: Int? = null
    var err: Exception? = null

    constructor(kind: String?, message: String?) : this() {
        this.kind = kind
        this.message = message
    }

    constructor(message: String?,throwable: Throwable): this() {
        this.message = message

    }

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