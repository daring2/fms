package com.gitlab.daring.fms.common.concurrent

import com.gitlab.daring.fms.common.config.getMillis
import com.gitlab.daring.fms.common.config.getOpt
import com.typesafe.config.Config
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit.MILLISECONDS

object ConcurrentUtils {

    fun newExecutor(c: Config): ThreadPoolExecutor {
        val size = c.getInt("size")
        val maxSize = c.getOpt { getInt("maxSize") } ?: size
        val keepAlive = c.getOpt { getMillis("keepAlive") } ?: 60000
        return ThreadPoolExecutor(
                size, maxSize, keepAlive, MILLISECONDS,
                LinkedBlockingQueue<Runnable>()
        )
    }

}