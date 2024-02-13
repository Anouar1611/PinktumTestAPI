package com.todoapipinktum.dto

import com.todoapipinktum.model.State
import java.util.*

data class TodoDTO (
    val id: String,
    val title: String,
    val description: String?,
    val createdAt: Date,
    var modifiedAt: Date,
)

