package com.kaspersky.test_server

import com.kaspresky.test_server.log.LoggerFactory

// todo logs
class Main {

    fun main(args: Array<String>) {
        // todo handle args for debugger?
        val logger = LoggerFactory.systemLogger()
        val desktop = Desktop(logger)
        desktop.startDevicesObserving()
    }

}