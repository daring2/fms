package com.gitlab.daring.fms.common.config

import com.typesafe.config.Config

fun Config.getMillis(key: String): Long {
    return getDuration(key).toMillis()
}