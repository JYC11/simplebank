package org.example.simplebank

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorld {
    @GetMapping("/hello-world")
    fun hello() = "Hello World!"
}