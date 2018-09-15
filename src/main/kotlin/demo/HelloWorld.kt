package demo

import spark.Spark.*
import demo.Game

class HelloWorld {
    companion object {
        @JvmStatic
        fun main(args : Array<String>) {
            webSocket("/game", Game::class)
            init()
        }



    }
}