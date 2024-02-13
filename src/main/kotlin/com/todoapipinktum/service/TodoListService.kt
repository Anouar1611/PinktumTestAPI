package com.todoapipinktum.service

import com.todoapipinktum.exception.NotFoundException
import com.todoapipinktum.model.Item
import com.todoapipinktum.model.State
import com.todoapipinktum.model.Todo
import com.todoapipinktum.repository.ItemRepository
import com.todoapipinktum.repository.TodoRepository
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class TodoListService(
    @Autowired val todoRepository: TodoRepository,
    @Autowired val itemRepository: ItemRepository,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun createTodo(title: String, description: String?): Todo {
        logger.info("Calling Todo List Service for creating a Todo with title {}", title)
        val date = Date()
        val todo = Todo(
            title = title,
            description = description,
            createdAt = date,
            modifiedAt = date,
        )
        return todoRepository.save(todo).also {
            logger.info("Todo list item with title {} is created!", todo.title)
        }
    }

    fun addItemToTodoList(todoId: String, name: String): Item {
        logger.info("Calling Todo List Service for adding a Todo item with name {}", name)
        val date = Date()
        val todoItem = Item(
            name = name,
            todoId = ObjectId(todoId),
            createdAt = date,
            modifiedAt = date,
        )
        val todo = getTodoByIdAssured(todoId)
        itemRepository.save(todoItem)
        todo.modifiedAt = date
        todoRepository.save(todo)
        return todoItem
    }

    fun getAllTodoItems(id: String): List<Item> {
        logger.info("Calling Todo List Service for getting a Todo items with id {}", id)
        return itemRepository.findAll()
    }

    fun getAllTodos(): List<Todo> {
        logger.info("Calling Todo List Service for getting all Todos")
        return todoRepository.findAll()
    }

    fun getTodoById(id: String): Todo? {
        logger.info("Calling Todo List Service for getting Todo with Id {}", id)
        return todoRepository.findById(id).orElse(null)
    }

    fun getItemById(id: String): Item? {
        logger.info("Calling Todo List Service for getting Item with Id {}", id)
        return itemRepository.findById(id).orElse(null)
    }

    fun deleteTodoById(id: String) {
        logger.info("Calling Todo List Service for deleting Todo with Id {}", id)
        val todo = getTodoByIdAssured(id)
        val todoId = todo.id
        itemRepository.deleteAllByTodoId(todoId)
        todoRepository.deleteTodoById(todoId.toString())
    }

    fun deleteItemTodoById(itemId: String) {
        logger.info("Calling Todo List Service for deleting Todo with Id {}", itemId)
        itemRepository.deleteById(itemId).also {
            logger.info("Item with Id {} was successfully deleted!", itemId)
        }
    }

    fun updateTodo(todoUpdate: Todo): Todo {
        logger.info("Calling Todo List Service for updating Todo Item with Id {}", todoUpdate.id.toString())
        val persistentTodo = getTodoByIdAssured(todoUpdate.id.toString())

        if (persistentTodo.title != todoUpdate.title) {
            updateTodoTitle(persistentTodo, todoUpdate.title, false)
        }

        if (persistentTodo.description != todoUpdate.description) {
            updateTodoDescription(persistentTodo, todoUpdate.description, false)
        }

        return todoRepository.save(persistentTodo)
    }

    fun updateTodoItem(todoItemUpdate: Item): Item {
        logger.info("Calling Todo List Service for updating Todo Item with Id {}", todoItemUpdate.id.toString())
        val persistentTodoItem = getItemTodoByIdAssured(todoItemUpdate.id.toString())

        if (persistentTodoItem.name != todoItemUpdate.name) {
            updateTodoItemName(persistentTodoItem, todoItemUpdate.name, false)
        }

        if (persistentTodoItem.state != todoItemUpdate.state) {
            updateTodoItemState(persistentTodoItem, todoItemUpdate.state, false)
        }

        return itemRepository.save(persistentTodoItem)
    }

    private fun updateTodoTitle(todo: Todo, title: String, save: Boolean = true): Todo {
        logger.info("Update todo title to {}", title)
        todo.title = title
        todo.modifiedAt = Date()

        return if (save) {
            todoRepository.save(todo)
        } else {
            todo
        }
    }

    private fun updateTodoDescription(todo: Todo, description: String?, save: Boolean = true): Todo {
        logger.info("Update todo description to {}", description)
        todo.description = description
        todo.modifiedAt = Date()

        return if (save) {
            todoRepository.save(todo)
        } else {
            todo
        }
    }

    private fun updateTodoItemName(item: Item, name: String?, save: Boolean = true): Item {
        logger.info("Update todo item name to {}", name)
        item.name = name
        item.modifiedAt = Date()

        return if (save) {
            itemRepository.save(item)
        } else {
            item
        }
    }

    private fun updateTodoItemState(item: Item, state: State, save: Boolean = true): Item {
        logger.info("Update todo item state to {}", state)
        item.state = state
        item.modifiedAt = Date()

        return if (save) {
            itemRepository.save(item)
        } else {
            item
        }
    }

    private fun getTodoByIdAssured(id: String): Todo {
        return getTodoById(id) ?: let {
            logger.warn("Todo with id: {} does not exist.", id)
            throw NotFoundException("Todo not found")
        }
    }

    private fun getItemTodoByIdAssured(id: String): Item {
        return getItemById(id) ?: let {
            logger.warn("Todo Item with id: {} does not exist.", id)
            throw NotFoundException("Item not found")
        }
    }


}