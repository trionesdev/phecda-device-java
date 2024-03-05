package com.trionesdev.phecda.device.contracts.common

object CommonConstants {

    const val Name: String = "name"
    const val Command: String = "command"

    //endregion
    const val READ_WRITE_R: String = "R"
    const val READ_WRITE_W: String = "W"
    const val READ_WRITE_RW: String = "RW"
    const val READ_WRITE_WR: String = "WR"


    const val ApiVersion: String = "v1"


    //region Constants related to Reading ValueTypes
    const val ValueTypeInt: String = "INT"
    const val ValueTypeFloat: String = "FLOAT"
    const val ValueTypeDouble: String = "DOUBLE"
    const val ValueTypeBool: String = "BOOL"
    const val ValueTypeString: String = "STRING"
    const val ValueTypeStruct: String = "STRUCT"
    const val ValueTypeArray: String = "ARRAY"
    const val ValueTypeBinary: String = "BINARY"
    //endregion

}