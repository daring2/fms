package com.gitlab.daring.fms.common.network

import org.junit.Test
import org.mockito.Mockito.*
import java.net.InetSocketAddress
import java.net.Socket

class ConfigurableSocketFactoryTest {

    @Test
    fun testCreateSocket() {
        val tf = TestSocketFactory()

        val s1 = tf.createSocket("h1", 1)
        verify(s1).connect(InetSocketAddress("h1", 1), 100)
        verify(s1).soTimeout = 101
        verifyNoMoreInteractions(s1)

        val s2 = tf.createSocket(null as String?, 1)
        verify(s2).connect(tf.nullAddress(1), 100)
        verify(s2).soTimeout = 101
        verifyNoMoreInteractions(s2)

        //TODO extend
    }

    class TestSocketFactory: ConfigurableSocketFactory(100, 101) {

        override fun newSocket() = mock(Socket::class.java)

        fun nullAddress(port: Int) = newSocketAddress(null, port)

    }

}