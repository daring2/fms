package com.gitlab.daring.fms.common.failsafe

import com.gitlab.daring.fms.common.config.getMillis
import com.gitlab.daring.fms.common.config.getOpt
import com.typesafe.config.Config
import net.jodah.failsafe.CircuitBreaker
import java.util.concurrent.TimeUnit.MILLISECONDS

object FailsafeUtils {

    fun newCircuitBreaker(threshold: Int, delay: Long): CircuitBreaker {
        return CircuitBreaker()
                .withFailureThreshold(threshold)
                .withDelay(delay, MILLISECONDS)
    }

    fun newCircuitBreaker(c: Config): CircuitBreaker {
        return CircuitBreaker()
                .withFailureThreshold(c.getInt("threshold"))
                .withSuccessThreshold(c.getOpt { getInt("successThreshold") } ?: 1)
                .withDelay(c.getMillis("delay"), MILLISECONDS)
    }

}