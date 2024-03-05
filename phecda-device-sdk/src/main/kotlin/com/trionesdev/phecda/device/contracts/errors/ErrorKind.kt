package com.trionesdev.phecda.device.contracts.errors

object ErrorKind {
    const val KindUnknown = "Unknown"
    const val KindDatabaseError = "Database"
    const val KindCommunicationError = "Communication"
    const val KindEntityDoesNotExist = "NotFound"
    const val KindContractInvalid = "ContractInvalid"
    const val KIND_SERVER_ERROR = "UnexpectedServerError"
    const val KindDuplicateName = "DuplicateName"
    const val KIND_INVALID_ID = "InvalidId"
    const val KIND_NOT_ALLOWED = "NotAllowed"
    const val KindServiceLocked = "ServiceLocked"
    const val KindOverflowError = "OverflowError"
    const val KindNaNError = "NaNError"
}