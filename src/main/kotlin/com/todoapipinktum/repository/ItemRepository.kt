package com.todoapipinktum.repository

import com.todoapipinktum.model.Item
import com.todoapipinktum.model.Todo
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : MongoRepository<Item, String> {

    fun deleteAllByTodoId(todoId: ObjectId)

}
