package com.todoapipinktum.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("items")
data class Item(
    @Id
    val id: ObjectId = ObjectId.get(),
    var name: String?,
    var state: State = State.TODO,
    val todoId: ObjectId,
    val createdAt: Date,
    var modifiedAt: Date,
)

enum class State {
    TODO, COMPLETED
}