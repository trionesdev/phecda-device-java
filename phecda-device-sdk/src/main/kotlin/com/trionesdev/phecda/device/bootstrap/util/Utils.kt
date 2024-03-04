package com.trionesdev.phecda.device.bootstrap.util

import cn.hutool.core.bean.BeanUtil
import com.alibaba.fastjson2.JSON

object Utils {
    fun mergeValues(dest: Any, src: Any) {
        var destMap: MutableMap<String, Any>? = null
        var srcMap: MutableMap<String, Any>? = null
        destMap = if (dest !is Map<*, *>) {
            JSON.parseObject(JSON.toJSONString(dest))
        } else {
            dest as MutableMap<String, Any>
        }
        srcMap = if (src !is Map<*, *>) {
            JSON.parseObject(JSON.toJSONString(src))
        } else {
            src as MutableMap<String, Any>
        }
        mergeMaps(destMap!!, srcMap!!)
        val destNew = JSON.parseObject(JSON.toJSONString(destMap), dest.javaClass)
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