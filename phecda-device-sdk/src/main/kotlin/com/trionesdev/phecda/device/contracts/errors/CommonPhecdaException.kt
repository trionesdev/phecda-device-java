package com.trionesdev.phecda.device.contracts.errors

class CommonPhecdaException() : RuntimeException(), PhecdaException {

    companion object {
        fun newCommonPhecdaException(kind: String?, message: String?, wrappedError: Exception?): CommonPhecdaException {
            return CommonPhecdaException().apply {
                this.kind = kind
                this.message = message
                this.err = wrappedError
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