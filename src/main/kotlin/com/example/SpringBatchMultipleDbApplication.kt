package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBatchMultipleDbApplication

fun main(args: Array<String>) {
    runApplication<SpringBatchMultipleDbApplication>(*args)
}
