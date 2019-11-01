package com.kaspresky.test_server.log.filter_log

import com.kaspresky.test_server.log.full_logger.FullLogger
import java.util.ArrayDeque
import java.util.Deque

internal class LoggerAnalyzer(
    private val fullLogger: FullLogger
) : FullLogger {

    private companion object {
        private const val SLASH_AT_THE_BEGINNING: Int = 40
        private const val SLASH_AT_THE_END: Int = 100
    }

    private val logStack: Deque<LogData> = ArrayDeque()
    private var logRecorder: LogRecorder = LogRecorder()

    override fun log(
        logType: FullLogger.LogType?,
        deviceName: String?,
        tag: String?,
        method: String?,
        text: String?
    ) {
        handleLog(
            key = "$logType$deviceName$tag$method$text",
            action = { fullLogger.log(logType, deviceName, tag, method, text) }
        )
    }

    private fun handleLog(key: String, action: () -> Unit) {
        val logData = LogData(key, action)
        val position = logStack.indexOf(logData)
        val answer = logRecorder.put(position, LogData(key, action))
        when (answer) {
            is RecordInProgress -> { return }
            is ReadyRecord -> {
                outputRecord(answer)
                updateState(answer)
            }
        }
    }

    private fun outputRecord(readyRecord: ReadyRecord) {
        // prepare the first and the last log for recorded Fragment if it's needed
        var fragmentStartString: String? = null
        var fragmentEndString: String? = null
        if (readyRecord.countOfRecordingStack > 0) {
            fragmentStartString = "/".repeat(SLASH_AT_THE_BEGINNING) +
                    "FRAGMENT IS REPEATED ${readyRecord.countOfRecordingStack} TIMES" +
                    "/".repeat(SLASH_AT_THE_BEGINNING)
            fragmentEndString = "/".repeat(SLASH_AT_THE_END)
        }
        // output record
        fragmentStartString?.let { fullLogger.log(text = fragmentStartString) }
        readyRecord.recordingStack.descendingIterator().forEach { it.logOutput.invoke() }
        fragmentEndString?.let { fullLogger.log(text = fragmentEndString) }
        // output remained part
        readyRecord.remainedStack.descendingIterator().forEach { it.logOutput.invoke() }
    }

    private fun updateState(readyRecord: ReadyRecord) {
        if (readyRecord.recordingStack.isNotEmpty()) {
            logStack.clear()
        }
        readyRecord.remainedStack.descendingIterator().forEach {
            logStack.addFirst(it)
        }
    }
}