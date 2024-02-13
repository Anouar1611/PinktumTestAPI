package com.todoapipinktum.dto

import com.todoapipinktum.model.State

data class ItemDTO (
    val id: String,
    val name: String?,
    val state: State
)