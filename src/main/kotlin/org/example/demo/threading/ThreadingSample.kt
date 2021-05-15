package org.example.demo.threading

import kotlin.concurrent.thread

class ThreadingSample {

    /**
     * Please be careful when running this code.
     */
    fun `JVM will throw an OutOfMemoryError when it tries to create too many threads`() {
        val num = 9000
        for (i in 1..num) {
            thread {
                println("$i: ${Thread.currentThread().name}")
                Thread.sleep(10000L)
            }
        }
    }
}

fun main() {
    val ts = ThreadingSample()
    ts.`JVM will throw an OutOfMemoryError when it tries to create too many threads`()
}
