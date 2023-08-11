package org.example.demo.coroutine

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

class CoroutineSample {

    fun `First Coroutine`() {
        println(Thread.currentThread().name)
        runBlocking {
            launch {
                println(Thread.currentThread().name)
                println("1")
                delay(1000L)
                println("2")
            }
            launch {
                println(Thread.currentThread().name)
                println("3")
            }
        }

        // main
        // main
        // 1
        // main
        // 3
        // 2
    }

    fun `Coroutine will be missed when there are no runBlocking`() {
        val scope = CoroutineScope(EmptyCoroutineContext)
        println(Thread.currentThread().name)
        scope.launch {
            delay(1000L)
            println(Thread.currentThread().name) // Will not be reached
        }

        // main
    }

    fun `Wait until a Coroutine is completed by launch and join`() {
        runBlocking {
            val job = launch { // Job
                println("1")
                delay(1000L)
                println("2")
            }
            job.join()
        }
    }

    fun `Wait until Coroutines are completed by async and await`() {
        runBlocking {
            println(Thread.currentThread().name)
            val deferred1 = async(Dispatchers.IO) { // Deferred<Int>
                println(Thread.currentThread().name)
                println("1")
                delay(1000L)
                println("2")
                1
            }
            println(deferred1) // Deferred object will be returned before the suspend function finishes
            val deferred2 = async(Dispatchers.IO) { // Deferred<Int>
                println(Thread.currentThread().name)
                println("3")
                delay(2000L)
                println("4")
                2
            }
            println(deferred2)
            println(deferred1.await() + deferred2.await()) // Wait until Deferred object is completed
            println(awaitAll(deferred1, deferred2)) // Wait until all the Deferred objects are completed and return the list of returned value

            // main
            // DefaultDispatcher-worker-1
            // 1
            // DeferredCoroutine{Active}@d9a1d755
            // DeferredCoroutine{Active}@1d736af5
            // DefaultDispatcher-worker-3
            // 3
            // 2
            // 4
            // 3
            // [1, 2]
        }
    }

    fun `Wait until Coroutines are completed by withContext`() {
        runBlocking {
            println(Thread.currentThread().name)
            val one = withContext(Dispatchers.IO) {
                println(Thread.currentThread().name)
                println("1")
                delay(2000L)
                println("2")
                1
            }
            println(one) // Wait until the previous Coroutine finishes
            val two = withContext(Dispatchers.IO) {
                println(Thread.currentThread().name)
                println("3")
                delay(1000L)
                println("4")
                2
            }
            println(two)

            // main
            // DefaultDispatcher-worker-1
            // 1
            // 2
            // 1
            // DefaultDispatcher-worker-1
            // 3
            // 4
            // 2
        }
    }

    fun `Coroutine can be cancelled`() {
        val scope = CoroutineScope(EmptyCoroutineContext) // Parent CoroutineScope
        println(Thread.currentThread().name)
        scope.launch { // Child CoroutineScope
            println(Thread.currentThread().name)
            println("1")
            delay(1000L)
            println("2") // Will not be reached

            // All the Coroutines which are the descendants of this CoroutineScope will be cancelled
            // It cannot be restarted
            scope.cancel()

            scope.launch {
                println("5") // Will not be reached
            }

            launch { // Grandchild CoroutineScope
                println("6") // Will not be reached
            }
        }
        scope.launch { // Child CoroutineScope
            println(Thread.currentThread().name)
            println("3")
            delay(2000L)
            println("4") // Will not be reached
        }

        // Wait until all the Coroutines are completed
        Thread.sleep(3000L)

        // main
        // DefaultDispatcher-worker-1
        // 1
        // DefaultDispatcher-worker-2
        // 3
        // 2
    }

    fun `Coroutine is light-weight compared with threading`() {
        runBlocking {
            val num = 1000000
            for (i in 1..num) {
                launch {
                    println("$i: ${Thread.currentThread().name}")
                    delay(10000L)
                }
            }
        }

        // ...
        // 999995: main
        // 999996: main
        // 999997: main
        // 999998: main
        // 999999: main
        // 1000000: main
    }

    fun `Do not block threads when you work with Coroutine`() {
        runBlocking {
            val num = 1000000
            for (i in 1..num) {
                launch {
                    println("$i: ${Thread.currentThread().name}")
                    Thread.sleep(10000L) // Block the main thread
                }
            }
        }

        // ... (So slow...)
        // 999995: main
        // 999996: main
        // 999997: main
        // 999998: main
        // 999999: main
        // 1000000: main
    }

    fun `CoroutineContext controls the state of Coroutine`() {
        runBlocking {
            println(Thread.currentThread().name)

            val exceptionHandler = CoroutineExceptionHandler { ctx, e ->
                println("Context: $ctx, Exception: $e")
            }

            // CoroutineContext = Job + CoroutineDispatcher + CoroutineName + CoroutineExceptionHandler
            val context = Job() + Dispatchers.Default + CoroutineName("Sample Coroutine") + exceptionHandler

            val scope = CoroutineScope(context)
            scope.launch {
                println(Thread.currentThread().name)
                withContext(Dispatchers.IO) { //
                    println(Thread.currentThread().name)
                }
            }
        }

        // main
        // DefaultDispatcher-worker-1
        // DefaultDispatcher-worker-1
    }

    fun `Error handling`() {
        val context = Job()
        val scope = CoroutineScope(context)
        val deferred = scope.async {
            // Should catch an Exception inside the same CoroutineScope
            try {
                delay(1000L)
                throw Exception("error")
            } catch (e: Exception) {
                e.message
            }
        }
        scope.launch {
            println("result: ${deferred.await()}")
        }
        Thread.sleep(2000L)

        // result: error
    }

    fun `suspend function inside Coroutine will be executed in sequence`() {
        runBlocking {
            func1()
            func2()
        }

        // 1
        // 2
        // 3
        // 4
    }

    suspend fun func1() {
        println("1")
        delay(2000L)
        println("2")
    }

    suspend fun func2() {
        println("3")
        delay(1000L)
        println("4")
    }
}

fun main() {
    val cs = CoroutineSample()
    cs.`suspend function inside Coroutine will be executed in sequence`()
}
