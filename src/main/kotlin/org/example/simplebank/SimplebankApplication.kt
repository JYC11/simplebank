package org.example.simplebank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SimplebankApplication

fun main(args: Array<String>) {
    runApplication<SimplebankApplication>(*args)
}
