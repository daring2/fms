package com.gitlab.daring.fms.common.concurrent

import com.typesafe.config.Config
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit.SECONDS

object ConcurrentUtils {

    fun newExecutor(c: Config): ThreadPoolExecutor {
        val size = c.getInt("size")
        //TODO support all params
        return ThreadPoolExecutor(
                size, size,
                60L, SECONDS,
                LinkedBlockingQueue<Runnable>()
        )
    }

}