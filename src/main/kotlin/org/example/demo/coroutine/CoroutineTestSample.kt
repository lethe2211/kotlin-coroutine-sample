package org.example.demo.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class CoroutineTestSample(
    private val dummyIORepository: DummyIORepository,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Main
) {

    suspend fun fetchData(n: Int): List<Int> {
        println("fetchData: ${Thread.currentThread().name}")

        return coroutineScope {
            println("coroutineScope: ${Thread.currentThread().name}")
            try {
                (1..n)
                    .toList()
                    .map {
                        async(coroutineDispatcher) {
                            println("async: ${Thread.currentThread().name}")
                            dummyIORepository.processHeavyIO(it)
                        }
                    }
                    .awaitAll()
            } catch (e: Exception) {
                e.stackTrace
                listOf()
            }
        }
    }
}

fun main() {
    runBlocking {
        val cts = CoroutineTestSample(
            dummyIORepository = DummyIORepositoryImpl(),
            coroutineDispatcher = Dispatchers.Main
        )
        println(cts.fetchData(100))
    }
}
