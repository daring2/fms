package com.gitlab.daring.fms.common.failsafe

import com.gitlab.daring.fms.common.config.getMillis
import com.typesafe.config.Config
import net.jodah.failsafe.CircuitBreaker
import java.util.concurrent.TimeUnit.MILLISECONDS

object FailsafeUtils {

    fun newCircuitBreaker(c: Config): CircuitBreaker {
        return CircuitBreaker()
                .withFailureThreshold(c.getInt("threshold"))
                .withDelay(c.getMillis("delay"), MILLISECONDS)
    }

}