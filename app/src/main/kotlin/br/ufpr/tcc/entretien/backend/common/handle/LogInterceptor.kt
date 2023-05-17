package br.ufpr.tcc.entretien.backend.common.handle

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LogInterceptor : HandlerInterceptor {
    companion object {
        private val LOGGER: Logger = LogManager.getLogger(LogInterceptor::class.java)
        private const val CORRELATION_ID_HEADER = "correlationId"
        private const val LOG_TAG = "log-interceptor-handler"
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val correlationId = request.getHeader(CORRELATION_ID_HEADER)

        if (correlationId.isNullOrEmpty()) {
            val newCorrelationId = UUID.randomUUID().toString()
            request.setAttribute(CORRELATION_ID_HEADER, newCorrelationId)
            LOGGER.info("Criado novo correlationId: $newCorrelationId", LOG_TAG)
        }

        return true
    }

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        // Executa após o processamento do handler
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        // Executa após a conclusão do processamento do handler
    }
}