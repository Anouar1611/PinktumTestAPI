package com.todoapipinktum.repository

import com.todoapipinktum.model.Todo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : MongoRepository<Todo, String> {

    fun findTodoById(id: String): Todo?

    fun deleteTodoById(id: String)

}