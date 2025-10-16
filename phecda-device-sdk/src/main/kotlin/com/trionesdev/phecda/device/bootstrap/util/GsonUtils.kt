package com.trionesdev.phecda.device.bootstrap.util

import com.google.gson.*
import com.google.gson.annotations.Expose


object GsonUtils {
    var gson: Gson = Gson()

    init {
        gson = GsonBuilder().disableHtmlEscaping()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .addSerializationExclusionStrategy(object : ExclusionStrategy {
                override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
                    val expose = fieldAttributes.getAnnotation<Expose?>(Expose::class.java)
                    return expose != null && !expose.serialize
                }

                override fun shouldSkipClass(aClass: Class<*>?): Boolean {
                    return false
                }
            })
            .addDeserializationExclusionStrategy(object : ExclusionStrategy {
                override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
                    val expose = fieldAttributes.getAnnotation<Expose?>(Expose::class.java)
                    return expose != null && !expose.deserialize
                }

                override fun shouldSkipClass(aClass: Class<*>?): Boolean {
                    return false
                }
            })
            .create()
    }

//    fun getGson(): Gson {
//        return gson
//    }

    fun toJson(obj: Any?): String? {
        return gson.toJson(obj)
    }
}