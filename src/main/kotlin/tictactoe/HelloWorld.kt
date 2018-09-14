package tictactoe

import spark.Spark.*

class HelloWorld {
    companion object {
        @JvmStatic
        fun main(args : Array<String>) {
            get("/hello") { req, res -> "Hello World" }
        }
    }
}