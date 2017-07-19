package com.gitlab.daring.fms.common.network

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class ConfigurableSocketFactoryTest {

    @Test
    fun testCreateSocket() {
        val tf = TestSocketFactory()

        val s1 = tf.createSocket("h1", 1)
        verify(s1).soTimeout = 101
        verify(s1).connect(InetSocketAddress("h1", 1), 100)
        verifyNoMoreInteractions(s1)

        val s2 = tf.createSocket(null as String?, 1)
        verify(s2).soTimeout = 101
        verify(s2).connect(tf.newAddress(null, 1), 100)
        verifyNoMoreInteractions(s2)

        val lh =  InetAddress.getLocalHost()
        val s3 = tf.createSocket("h1", 1, lh, 2)
        verify(s3).soTimeout = 101
        verify(s3).bind(InetSocketAddress(lh, 2))
        verify(s3).connect(InetSocketAddress("h1", 1), 100)
        verifyNoMoreInteractions(s3)

        //TODO extend
    }

    @Test
    fun testNewSocketAddress() {
        val tf = TestSocketFactory()
        assertEquals(InetSocketAddress("h1", 1), tf.newAddress("h1", 1))
        val nullAddress = InetAddress.getByName(null)
        assertEquals(InetSocketAddress(nullAddress, 1), tf.newAddress(null, 1))
    }

    class TestSocketFactory: ConfigurableSocketFactory(100, 101) {

        override fun newSocket() = mock(Socket::class.java)

        fun newAddress(host: String?, port: Int) = newSocketAddress(host, port)

    }

}