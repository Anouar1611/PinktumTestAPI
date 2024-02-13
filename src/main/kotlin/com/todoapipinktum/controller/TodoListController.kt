package com.todoapipinktum.controller

import com.todoapipinktum.dto.ItemDTO
import com.todoapipinktum.dto.TodoDTO
import com.todoapipinktum.ApiErrorException
import com.todoapipinktum.GlobalCodes
import com.todoapipinktum.exception.NotFoundException
import com.todoapipinktum.model.Item
import com.todoapipinktum.model.State
import com.todoapipinktum.model.Todo
import com.todoapipinktum.service.TodoListService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/todolist")
@CrossOrigin(origins = ["http://localhost:8081"])
class TodoListController (
    val todoListService: TodoListService,
){
    val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping
    fun getTodos(): ResponseEntity<List<TodoDTO>> {
        val listTodos = todoListService.getAllTodos().map { mapTodo(it) }
        return ResponseEntity.ok(listTodos)
    }

    @GetMapping("/{id}")
    fun getTodoById(
        @PathVariable("id") id: String,
    ): ResponseEntity<TodoDTO> {
        val todo = todoListService.getTodoById(id) ?: let {
            logger.warn("Todo with id: {} does not exist.", id)
            throw ApiErrorException(GlobalCodes.NOT_FOUND, HttpStatus.NOT_FOUND)
        }
        return ResponseEntity.ok(mapTodo(todo))
    }

    @GetMapping("/{id}/items")
    fun getTodoItems(
        @PathVariable("id") id: String,
    ): ResponseEntity<List<ItemDTO>> {
        val todo = todoListService.getTodoById(id) ?: let {
            logger.warn("Todo with id: {} does not exist.", id)
            throw ApiErrorException(GlobalCodes.NOT_FOUND, HttpStatus.NOT_FOUND)
        }
        return ResponseEntity.ok(todoListService.getAllTodoItems(todo.id.toString()).map { mapItem(it) })
    }

    @PostMapping
    fun createTodo(
        @RequestBody request: TodoCreateRequest
    ): ResponseEntity<TodoDTO> {
        if (request.title.isEmpty()) {
            logger.warn("Title cannot be empty")
            throw ApiErrorException(GlobalCodes.EMPTY_REQUEST_TITLE, HttpStatus.BAD_REQUEST)
        }
        val createdTodo = todoListService.createTodo(request.title, request.description)
        return ResponseEntity.ok(mapTodo(createdTodo))
    }

    @PostMapping("/{id}/item")
    fun addItemToTodo(
        @PathVariable("id") id: String,
        @RequestBody request: ItemCreateRequest,
    ): ResponseEntity<ItemDTO> {
        if (request.name.isEmpty()) {
            logger.warn("Item name cannot be empty")
            throw ApiErrorException(GlobalCodes.EMPTY_REQUEST_ITEM_NAME, HttpStatus.BAD_REQUEST)
        }
        val todo = todoListService.getTodoById(id) ?: let {
            logger.warn("Todo with id: {} does not exist.", id)
            throw ApiErrorException(GlobalCodes.NOT_FOUND, HttpStatus.NOT_FOUND)
        }
        val createdTodoItem = todoListService.addItemToTodoList(todo.id.toString(), request.name)
        return ResponseEntity.ok(mapItem(createdTodoItem))
    }

    @DeleteMapping("/{id}")
    fun deleteTodo(
        @PathVariable("id") id: String,
    ): ResponseEntity<Unit> {
        val todo = todoListService.getTodoById(id) ?: let {
            logger.warn("Todo with id: {} does not exist.", id)
            throw ApiErrorException(GlobalCodes.NOT_FOUND, HttpStatus.NOT_FOUND)
        }
        todoListService.deleteTodoById(todo.id.toString())
        return ResponseEntity.ok().body(Unit)
    }

    @DeleteMapping("/item/{itemId}")
    fun deleteTodoItem(
        @PathVariable("itemId") itemId: String,
    ): ResponseEntity<Unit> {
        val itemToDelete = todoListService.getItemById(itemId) ?: let {
            logger.warn("Todo Item with id: {} does not exist.", itemId)
            throw ApiErrorException(GlobalCodes.NOT_FOUND, HttpStatus.NOT_FOUND)
        }
        if (itemToDelete.state == State.TODO) {
            logger.warn("Todo item with name: {} and state {} cannot be deleted, it must be in {} state", itemId, State.TODO, State.COMPLETED)
            throw ApiErrorException(GlobalCodes.INVALID_ITEM_STATE, HttpStatus.BAD_REQUEST)
        }
        todoListService.deleteItemTodoById(itemToDelete.id.toString())
        return ResponseEntity.ok().body(Unit)
    }

    @PutMapping("/{id}")
    fun updateTodo(
        @PathVariable("id") id: String,
        @RequestBody request: TodoUpdateRequest,
    ): ResponseEntity<TodoDTO> {
        if (request.title.isEmpty()) {
            logger.warn("Title cannot be empty")
            throw ApiErrorException(GlobalCodes.EMPTY_REQUEST_TITLE, HttpStatus.BAD_REQUEST)
        }
        val todo = todoListService.getTodoById(id) ?: let {
            logger.warn("Todo with id: {} does not exist.", id)
            throw ApiErrorException(GlobalCodes.NOT_FOUND, HttpStatus.NOT_FOUND)
        }
        todo.title = request.title
        todo.description = request.description
        try {
            val updatedTodo = todoListService.updateTodo(todo)
            return ResponseEntity.ok(mapTodo(updatedTodo))
        } catch (ex: NotFoundException) {
            logger.warn("Cannot update the todo")
            throw ApiErrorException(GlobalCodes.NOT_FOUND, HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/{id}/items/{itemId}")
    fun updateTodoItem(
        @PathVariable("id") id: String,
        @PathVariable("itemId") itemId: String,
        @RequestBody request: ItemUpdateRequest,
    ): ResponseEntity<ItemDTO> {
        if (request.name.isEmpty()) {
            logger.warn("Item Name cannot be empty")
            throw ApiErrorException(GlobalCodes.EMPTY_REQUEST_ITEM_NAME, HttpStatus.BAD_REQUEST)
        }
        val itemTodo = todoListService.getItemById(itemId) ?: let {
            logger.warn("Todo Item with id: {} does not exist.", itemId)
            throw ApiErrorException(GlobalCodes.NOT_FOUND, HttpStatus.NOT_FOUND)
        }
        if (itemTodo.state == State.COMPLETED) {
            logger.warn("Todo Item with id: {} cannot be updated in state {}.", itemId, State.COMPLETED)
            throw ApiErrorException(GlobalCodes.INVALID_ITEM_STATE, HttpStatus.BAD_REQUEST)
        }
        itemTodo.name = request.name
        itemTodo.state = request.state
        try {
            val updatedTodoItem = todoListService.updateTodoItem(itemTodo)
            return ResponseEntity.ok(mapItem(updatedTodoItem))
        } catch (ex: NotFoundException) {
            logger.warn("Cannot update the todo item")
            throw ApiErrorException(GlobalCodes.NOT_FOUND, HttpStatus.NOT_FOUND)
        }
    }

    private fun mapTodo(source: Todo): TodoDTO {
        return source.run {
            TodoDTO(
               id = this.id.toString(),
               title = this.title,
               description = this.description,
               createdAt = this.createdAt,
               modifiedAt = this.modifiedAt,
            )
        }
    }

    private fun mapItem(source: Item): ItemDTO {
        return source.run {
            ItemDTO(
                id = this.id.toString(),
                name = this.name,
                state = this.state
            )
        }
    }

    data class TodoCreateRequest(
        val title: String,
        val description: String?,
    )

    data class TodoUpdateRequest(
        val title: String,
        val description: String?,
    )

    data class ItemCreateRequest(
        val name: String,
    )

    data class ItemUpdateRequest(
        val name: String,
        val state: State,
    )

}