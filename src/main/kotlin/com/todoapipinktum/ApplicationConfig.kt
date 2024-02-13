package com.todoapipinktum

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "app")
class ApplicationConfig (
    val mongo: Mongo,
)

class Mongo (
    var uri: String,
    var fallback: MongoFallback,
)
class MongoFallback(
    var dbname: String,
    var appname: String,
)