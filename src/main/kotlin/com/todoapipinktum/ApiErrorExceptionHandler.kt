package com.todoapipinktum

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice(basePackages = ["com.todoapipinktum"])
class ApiErrorExceptionHandler : ResponseEntityExceptionHandler() {

    private val localLogger  = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ApiErrorException::class)
    fun handleApiErrorException(ex: ApiErrorException, request: WebRequest) : ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers["todo-code"] = listOf(ex.code)
        localLogger.info("Error code: {}", ex.code)
        return ResponseEntity(Unit, headers, ex.status)
    }
}