package com.trionesdev.phecda.device.sdk.messaging

object ValueTypeMapping {
    val deviceToPhecdaValueType = mapOf(
        "Bool" to "BOOL",
        "String" to "STRING",
        "Uint8" to "INT",
        "Uint16" to "INT",
        "Uint32" to "LONG",
        "Uint64" to "LONG",
        "Int8" to "INT",
        "Int16" to "INT",
        "Int32" to "INT",
        "Int64" to "LONG",
        "Float32" to "FLOAT",
        "Float64" to "DOUBLE",
        "Binary" to "BINARY",
        "BoolArray" to "ARRAY_BOOL",
        "StringArray" to "ARRAY_STRING",
        "Uint8Array" to "ARRAY_INT",
        "Uint16Array" to "ARRAY_INT",
        "Uint32Array" to "ARRAY_LONG",
        "Uint64Array" to "ARRAY_LONG",
        "Int8Array" to "ARRAY_INT",
        "Int16Array" to "ARRAY_INT",
        "Int32Array" to "ARRAY_INT",
        "Int64Array" to "ARRAY_LONG",
        "Float32Array" to "ARRAY_FLOAT",
        "Float64Array" to "ARRAY_DOUBLE",
        "Object" to "STRUCT",
    )

    val phecdaToDeviceValueType = mapOf(
        "BOOL" to "Bool",
        "STRING" to "String",
        "INT" to "Int32",
        "LONG" to "Int64",
        "FLOAT" to "Float32",
        "DOUBLE" to "Float64",
        "BINARY" to "Binary",
        "ARRAY_BOOL" to "BoolArray",
        "ARRAY_STRING" to "StringArray",
        "ARRAY_INT" to "Int32Array",
        "ARRAY_LONG" to "Int64Array",
        "ARRAY_FLOAT" to "Float32Array",
        "ARRAY_DOUBLE" to "Float64Array",
        "STRUCT" to "Object",
    )

}