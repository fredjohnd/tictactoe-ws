package tictactoe

import spark.Spark.*

object HelloWorld {
    fun main(args: Array<String>) {
        get("/hello") { req, res -> "Hello World" }
    }
}