package com.todoapipinktum.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "todos")
data class Todo(
    @Id
    val id: ObjectId = ObjectId.get(),
    var title: String,
    var description: String?,
    val createdAt: Date,
    var modifiedAt: Date,
)
