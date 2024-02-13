package com.todoapipinktum

import org.springframework.http.HttpStatus

class ApiErrorException(val code: String, val status: HttpStatus) : Exception()

object GlobalCodes {
    const val EMPTY_REQUEST_TITLE = "empty-title-error"
    const val EMPTY_REQUEST_ITEM_NAME = "empty-item-name-error"
    const val INVALID_ITEM_STATE = "invalid-item-state"
    const val NOT_FOUND = "not-found"
}