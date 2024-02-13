package com.todoapipinktum

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(ApplicationConfig::class)
open class TodoListPinktumApplication

fun main(args: Array<String>) {
    runApplication<TodoListPinktumApplication>(*args)
}