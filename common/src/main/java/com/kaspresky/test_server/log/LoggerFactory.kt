package com.kaspresky.test_server.log

import com.kaspresky.test_server.log.filter_log.FullLoggerFilteredByDeviceProvider
import com.kaspresky.test_server.log.full_logger.FullLogger
import com.kaspresky.test_server.log.logger.Logger
import com.kaspresky.test_server.log.logger.LoggerImpl

/**
 * The singleton to provide Logger interface and to hide an implementation
 */
object LoggerFactory {

    private val fullLogger: FullLogger = FullLoggerFilteredByDeviceProvider()

    fun getLogger(tag: String, deviceName: String? = null): Logger =
        LoggerImpl(tag, deviceName, fullLogger)
}