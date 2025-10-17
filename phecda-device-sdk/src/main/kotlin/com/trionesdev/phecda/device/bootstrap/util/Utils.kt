package com.trionesdev.phecda.device.bootstrap.util

import cn.hutool.core.bean.BeanUtil
import com.google.gson.reflect.TypeToken

object Utils {
    fun mergeValues(dest: Any, src: Any) {
        var destMap: MutableMap<String, Any>? = null
        var srcMap: MutableMap<String, Any>? = null
        destMap = if (dest !is Map<*, *>) {
//            JSON.parseObject(JSON.toJSONString(dest))
            GsonUtils.fromJson(GsonUtils.toJson(dest), object : TypeToken<MutableMap<String, Any>>() {}.type)
        } else {
            dest as MutableMap<String, Any>
        }
        srcMap = if (src !is Map<*, *>) {
//            JSON.parseObject(JSON.toJSONString(src))
            GsonUtils.fromJson(GsonUtils.toJson(src), object : TypeToken<MutableMap<String, Any>>() {}.type)
        } else {
            src as MutableMap<String, Any>
        }
        mergeMaps(destMap!!, srcMap!!)
//        val destNew = JSON.parseObject(JSON.toJSONString(destMap), dest.javaClass)
        val destNew = GsonUtils.parseObject(destMap, dest.javaClass)
        BeanUtil.copyProperties(destNew, dest, true)
    }

    fun mergeMaps(dest: MutableMap<String, Any>, src: MutableMap<String, Any>) {
        for ((key, value) in src) {
            if (!dest.containsKey(key)) {
                dest[key] = value
                continue
            }
            if (dest[key] is Map<*, *>) {
                dest[key]?.let { mergeValues(it, value) }
                continue
            }
            dest[key] = value
        }
    }

}