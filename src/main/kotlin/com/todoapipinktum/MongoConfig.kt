package com.todoapipinktum

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.util.*

@Configuration
@EnableMongoRepositories(basePackages = ["com.todoapipinktum.repository"], mongoTemplateRef = "todoMongoTemplate")
@EnableConfigurationProperties
open class MongoConfig (@Autowired config: ApplicationConfig) : AbstractMongoClientConfiguration() {

        val mongo: Mongo = config.mongo

        override fun getDatabaseName(): String {
            val conString = ConnectionString(mongo.uri)
            return conString.database ?: mongo.fallback.dbname
        }

        @Bean(name = ["todoMongoClient"])
        override fun mongoClient(): MongoClient {
            val conString = ConnectionString(mongo.uri)
            val setting = MongoClientSettings.builder()
                .applyConnectionString(conString)
            if (conString.applicationName == null) {
                setting.applicationName(mongo.fallback.appname)
            }
            return MongoClients.create(setting.build())
        }

        @Primary
        @Bean(name = ["todoMongoDBFactory"])
        open fun mongoDatabaseFactory(
            @Qualifier("todoMongoClient") mongoClient: MongoClient,
        ): MongoDatabaseFactory {
            return SimpleMongoClientDatabaseFactory(mongoClient, databaseName)
        }

        @Bean(name = ["todoMongoTemplate"])
        open fun mongoTemplate(@Qualifier("todoMongoDBFactory") mongoDatabaseFactory: MongoDatabaseFactory): MongoTemplate? {
            return MongoTemplate(mongoDatabaseFactory)
        }
        override fun getMappingBasePackages(): MutableCollection<String> {
            return Collections.singleton("com.todoapipinktum")
        }
}
