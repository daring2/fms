package com.gitlab.daring.fms.common.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

object ConfigUtils {

    fun configFromString(str: String): Config {
        return ConfigFactory.parseString(str)
    }

}