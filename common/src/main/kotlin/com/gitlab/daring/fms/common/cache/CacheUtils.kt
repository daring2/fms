package com.gitlab.daring.fms.common.cache

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader

object CacheUtils {

    //TODO consider to user caffeine

    fun <K, V> newCache(spec: String, loader: (K?) -> V): Cache<K, V> {
        val cl = CacheLoader.from(loader)
        return CacheBuilder.from(spec).build(cl)
    }
}