package com.todoapipinktum

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter

@Configuration
open class TodoListRequestLoggingFilter {

    @Bean
    open fun logFilter(): CommonsRequestLoggingFilter {
        val filter = CommonsRequestLoggingFilter()
        filter.setBeforeMessageSuffix(" ")
        filter.setBeforeMessagePrefix("REQUEST ")
        filter.setAfterMessagePrefix("END REQUEST : ")
        filter.setAfterMessageSuffix(" ")
        return filter
    }
}