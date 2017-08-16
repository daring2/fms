package com.gitlab.daring.fms.common.util

import com.gitlab.daring.fms.common.config.configFromProps
import java.time.Duration

object CommonUtils {

    fun parseDuration(str: String): Duration {
        return configFromProps("p" to str).getDuration("p")
    }

}