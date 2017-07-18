package com.gitlab.daring.fms.common.network

import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.net.InetSocketAddress
import java.net.Socket

class ConfigurableSocketFactoryTest {

    @Test
    fun testCreateSocket() {
        val tf = TestSocketFactory()
        val s1 = tf.createSocket("h1", 1)
        verify(s1).connect(InetSocketAddress("h1", 1), 100)
        verify(s1).soTimeout = 101
    }

    class TestSocketFactory: ConfigurableSocketFactory(100, 101) {
        override fun newSocket() = mock(Socket::class.java)
    }

}