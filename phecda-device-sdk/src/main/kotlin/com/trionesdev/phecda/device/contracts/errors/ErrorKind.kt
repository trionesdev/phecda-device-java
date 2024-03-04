package com.trionesdev.phecda.device.contracts.errors

object ErrorKind {
    const val KindUnknown = "Unknown"
    const val KindDatabaseError = "Database"
    const val KindCommunicationError = "Communication"
    const val KindEntityDoesNotExist = "NotFound"
    const val KindContractInvalid = "ContractInvalid"
    const val KindServerError = "UnexpectedServerError"
    const val KindDuplicateName = "DuplicateName"
    const val KindInvalidId = "InvalidId"
    const val KindOverflowError = "OverflowError"
    const val KindNaNError = "NaNError"
}