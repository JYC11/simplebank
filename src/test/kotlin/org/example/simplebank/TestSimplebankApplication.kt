package org.example.simplebank

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<SimplebankApplication>().with(TestcontainersConfiguration::class).run(*args)
}
