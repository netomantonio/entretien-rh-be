package br.ufpr.tcc.entretien.backend.common.handle

import br.ufpr.tcc.entretien.backend.common.error.ApiError
import br.ufpr.tcc.entretien.backend.common.error.BusinessErrorCode
import br.ufpr.tcc.entretien.backend.common.error.FieldViolation
import br.ufpr.tcc.entretien.backend.common.exception.interview.ContentNotFoundException
import br.ufpr.tcc.entretien.backend.common.exception.interview.UnavailableTimeException
import br.ufpr.tcc.entretien.backend.common.exception.interview.UserIsNotAuthorizedException
import br.ufpr.tcc.entretien.backend.common.exception.jwt.InvalidTokenException
import br.ufpr.tcc.entretien.backend.common.exception.jwt.TokenGeneratorException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.validation.ConstraintViolationException

@ControllerAdvice
class ExceptionHandlerAdvice : ResponseEntityExceptionHandler() {

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ApiError> {
        val apiError = ApiError(message = "An unexpected error occurred")
        return ResponseEntity(apiError, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ApiError> {
        val apiError = ApiError(message = "invalid argument")
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<ApiError> {
        val apiError = ApiError(message = "precondition failure")
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<ApiError> {
        val apiError = ApiError(message = "no such element" )
        return  ResponseEntity(apiError, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ContentNotFoundException::class)
    fun handleContentNotFoundException(ex: ContentNotFoundException): ResponseEntity<ApiError> {
        val apiError = ApiError(message = "resource not found" )
        return  ResponseEntity(apiError, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UnavailableTimeException::class)
    fun handleUnavailableTimeException(
        ex: UnavailableTimeException
    ) : ResponseEntity<ApiError> {
        val apiError = ApiError(
            status = BusinessErrorCode.UNAVAILABLE_TIME.code,
            message = BusinessErrorCode.UNAVAILABLE_TIME.description
        )
        return ResponseEntity(apiError, HttpStatus.CONFLICT)
    }
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: WebRequest
    ): ResponseEntity<ApiError> {
        val fieldViolations = ex.constraintViolations.map {
            FieldViolation(it.propertyPath.last().name, it.message)
        }
        val apiError = ApiError(message = "Invalid request parameters", fieldViolations = fieldViolations)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(
        ex: InvalidTokenException,
        request: WebRequest
    ) : ResponseEntity<ApiError> {
        val fieldViolation = FieldViolation(
            field = "token",
            description = BusinessErrorCode.INVALID_TOKEN.description
        )
        val apiError = ApiError(
            status = BusinessErrorCode.INVALID_TOKEN.code,
            message = "Please try with a valid token",
            fieldViolations = listOf(fieldViolation)

        )
        return ResponseEntity(apiError, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(TokenGeneratorException::class)
    fun handleTokenGeneratorException(
        ex: TokenGeneratorException,
        request: WebRequest
    ) : ResponseEntity<ApiError> {
        val fieldViolation = FieldViolation(
            field = "token",
            description = ex.message.toString()
        )
        val apiError = ApiError(message = "Invalid Credentials. Please try again with the correct credentials.", fieldViolations = listOf(fieldViolation))

        return ResponseEntity(apiError, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(UserIsNotAuthorizedException::class)
    fun handleUserIsNotAuthorizedException(
        ex: UserIsNotAuthorizedException,
        request: WebRequest
    ) : ResponseEntity<ApiError> {
        val fieldViolation = FieldViolation(
            field = "candidateId",
            description = "user does not have permission to perform this action"
        )
        val apiError = ApiError(message = ex.message.toString(), fieldViolations = listOf(fieldViolation))

        return ResponseEntity(apiError, HttpStatus.FORBIDDEN)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val fieldViolations = ex.bindingResult.allErrors.map {
            if (it is FieldError) {
                FieldViolation(it.field, it.defaultMessage ?: "")
            } else {
                FieldViolation("", it.defaultMessage ?: "")
            }
        }
        val apiError = ApiError(HttpStatus.BAD_REQUEST.toString(), "Invalid request parameters", fieldViolations)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }
}