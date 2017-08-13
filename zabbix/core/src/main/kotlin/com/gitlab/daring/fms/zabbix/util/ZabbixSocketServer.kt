package com.gitlab.daring.fms.zabbix.util

import org.slf4j.LoggerFactory
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean

abstract class ZabbixSocketServer : AutoCloseable {

    protected val logger = LoggerFactory.getLogger(javaClass)

    private val started = AtomicBoolean()

    @Volatile
    internal var serverSocket: ServerSocket? = null

    /**
     * Delay after socket accept errors
     */
    @Volatile
    var acceptErrorDelay = 100L

    val isStarted get() = started.get()

    abstract fun executor(): ExecutorService

    fun start() {
        if (started.compareAndSet(false, true))
            executor().execute(this::run)
    }

    fun stop() {
        if (started.compareAndSet(true, false)) {
            serverSocket?.close()
            serverSocket = null
        }
    }

    override fun close() = stop()

    private fun run() {
        createServerSocket().use {
            serverSocket = it
            while (isStarted && !it.isClosed) try {
                val socket = it.accept()
                executor().execute { tryProcess(socket) }
            } catch (e: Exception) {
                onError("accept", e)
                if (isStarted)
                    Thread.sleep(acceptErrorDelay)
            }
        }
    }

    protected abstract fun createServerSocket(): ServerSocket

    private fun tryProcess(socket: Socket) {
        try {
            socket.use(this::process)
        } catch (e: Exception) {
            onError("process", e)
        }
    }

    protected abstract fun process(socket: Socket)

    protected open fun onError(action: String, e: Exception) {
        if (isStarted) logger.warn("$action error", e)
    }

}