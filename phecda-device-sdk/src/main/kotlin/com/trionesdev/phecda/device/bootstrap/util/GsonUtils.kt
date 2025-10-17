package com.trionesdev.phecda.device.bootstrap.util

import com.google.gson.*
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


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

    fun parseObject(obj: Any): JsonObject? {
       return gson.toJsonTree( obj).asJsonObject
    }

    fun <T> parseObject(obj: Any,clazz: Class<T>?): T? {
        return if (obj is String){
            gson.fromJson(obj, clazz)
        }else{
            gson.fromJson(gson.toJson(obj), clazz)
        }
    }

    fun <T> fromJson(json: String?, clazz: Class<T>?): T? {
        return gson.fromJson(json, clazz)
    }

    fun <T> fromJson(json: String?,  typeOfT: Type): T? {
        return gson.fromJson(json, typeOfT)
    }

    fun <T> fromJson(json: JsonElement?, typeToken: TypeToken<T>): T? {
        return gson.fromJson(json, typeToken)
    }


}