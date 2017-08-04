package com.gitlab.daring.fms.zabbix.agent.active

import com.gitlab.daring.fms.common.config.ConfigUtils.configFromString
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.ThreadPoolExecutor

class AgentActiveClientTest {

    @Test
    fun testInit() {
        val c = configFromString("{ port=1, readTimeout=2s, executor { size=3, maxSize=4 } }")
        val cl = AgentActiveClient(c)
        assertEquals(1, cl.port)
        assertEquals(2000, cl.readTimeout)
        val exec = cl.executor as ThreadPoolExecutor
        assertEquals(3, exec.corePoolSize)
        assertEquals(4, exec.maximumPoolSize)
    }

    //TODO implement
    
}