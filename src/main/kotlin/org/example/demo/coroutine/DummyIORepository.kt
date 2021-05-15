package org.example.demo.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

interface DummyIORepository {
    @Throws(Exception::class) // Just for testing purpose
    suspend fun processHeavyIO(num: Int): Int

    @Throws(Exception::class)
    suspend fun throwException()
}

class DummyIORepositoryImpl(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Main
) : DummyIORepository {

    override suspend fun processHeavyIO(num: Int): Int {
        return withContext(coroutineDispatcher) {
            println("Start processHeavyIO: ${Thread.currentThread().name}")

            // Heavy IO
            delay(100L)

            println("Finish processHeavyIO: ${Thread.currentThread().name}")

            num
        }
    }

    override suspend fun throwException() {
        withContext(coroutineDispatcher) {
            throw Exception("Always throw an Exception")
        }
    }
}
