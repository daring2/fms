package com.gitlab.daring.fms.common.util

fun AutoCloseable.closeQuietly() {
    try {
        close()
    } catch (e: Exception) {
        // ignore
    }
}