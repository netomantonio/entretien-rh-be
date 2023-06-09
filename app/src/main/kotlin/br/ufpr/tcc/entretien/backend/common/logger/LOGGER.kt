package br.ufpr.tcc.entretien.backend.common.logger

import org.slf4j.Logger
import org.slf4j.LoggerFactory


class LOGGER(private val logger: Logger) {
    companion object {
        fun getLogger(clazz: Class<*>): LOGGER {
            val logger = LoggerFactory.getLogger(clazz)
            return LOGGER(logger)
        }
    }

    fun info(logTag: String, message: String, additionalValues: Map<String, String>? = null) {
        val logMessage = mutableMapOf<String, String>()
        additionalValues.let {
            it?.forEach { (key, value) ->
                logMessage[key] = value
            }
        }
        logMessage["LOG_TAG"] = logTag
        if (additionalValues.isNullOrEmpty()) logger.info(message)
        else logger.info("$message: {}", logMessage)
    }

    fun error(logTag: String, message: String?, stackTrace: Array<StackTraceElement>, additionalValues: Map<String, String>? = null) {
        logger.info(message, logTag, stackTrace, additionalValues)
    }
}
