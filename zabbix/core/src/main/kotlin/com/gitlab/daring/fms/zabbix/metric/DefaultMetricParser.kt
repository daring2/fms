package com.gitlab.daring.fms.zabbix.metric

import com.gitlab.daring.fms.common.json.JsonUtils.YamlMapper
import com.gitlab.daring.fms.common.json.readList
import com.gitlab.daring.fms.zabbix.model.Item
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache

class DefaultMetricParser(
        val cacheSize: Long
) : MetricParser {

    val cache = createCache()

    private fun createCache(): LoadingCache<String, Metric> {
        val cl = CacheLoader.from(this::parseMetric)
        return CacheBuilder.newBuilder().maximumSize(cacheSize).build(cl)
    }

    override fun getMetric(item: Item): Metric {
        return cache[item.key].copy(item = item)
    }

    private fun parseMetric(key: String?): Metric {
        if (key == null) throw NullPointerException("key")
        val i = key.indexOf('[')
        if (i <= 0) return Metric(key, emptyList())
        val params = YamlMapper.readList<String>(key.substring(i))
        return Metric(key.substring(0, i), params)
    }

}