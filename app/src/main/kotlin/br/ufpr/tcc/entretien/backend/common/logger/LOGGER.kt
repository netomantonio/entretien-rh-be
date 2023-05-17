package br.ufpr.tcc.entretien.backend.common.logger

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.message.MapMessage

class LOGGER(private val logger: Logger) {
    companion object {
        fun getLogger(clazz: Class<*>): LOGGER {
            val logger = LogManager.getLogger(clazz)
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
        logger.info("$message: {}", additionalValues, logger.level, logMessage, logTag)
    }

    fun error(logTag: String, message: String?, stackTrace: Array<StackTraceElement>, additionalValues: Map<String, String>? = null) {
        logger.info(message, logger.level, logTag, stackTrace, additionalValues)
    }
}

class MutableMapMessage<K : MapMessage<K, V>?, V> : MapMessage<K,V>()
