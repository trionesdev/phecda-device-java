package com.trionesdev.phecda.device.contracts.errors

object ErrorKind {
    const val KIND_UN_KNOW = "Unknown"
    const val KIND_DATABASE_ERROR = "Database"
    const val KIND_COMMUNICATION_ERROR = "Communication"
    const val KIND_ENTITY_DOSE_NOT_EXIST = "NotFound"
    const val KIND_CONTRACT_INVALID = "ContractInvalid"
    const val KIND_SERVER_ERROR = "UnexpectedServerError"
    const val KIND_DUPLICATE_NAME = "DuplicateName"
    const val KIND_INVALID_ID = "InvalidId"
    const val KIND_NOT_ALLOWED = "NotAllowed"
    const val KIND_SERVICE_LOCKED = "ServiceLocked"
    const val KIND_OVERFLOW_ERROR = "OverflowError"
    const val KIND_NAN_ERROR = "NaNError"
}