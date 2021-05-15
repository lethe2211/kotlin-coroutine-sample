package org.example.demo.coroutine

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.lang.Exception

@ExperimentalCoroutinesApi
internal class CoroutineTestSampleTest {

    lateinit var coroutineTestSample: CoroutineTestSample
    lateinit var dummyIORepository: DummyIORepository
    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        dummyIORepository = mock()
        coroutineTestSample = CoroutineTestSample(
            dummyIORepository = dummyIORepository,
            coroutineDispatcher = dispatcher
        )
    }

    @AfterEach
    fun tearDown() {
        reset(dummyIORepository)
        dispatcher.cleanupTestCoroutines()
    }

    @Nested
    inner class FetchDataTest {
        @Test
        fun `Returns the list of 1 to n in success cases`() {
            dispatcher.runBlockingTest {
                for (i in 1..10) {
                    whenever(dummyIORepository.processHeavyIO(eq(i)))
                        .thenReturn(i)
                }

                val expected = (1..10).toList()
                val actual = coroutineTestSample.fetchData(10)
                assertEquals(expected, actual)

                for (i in 1..10) {
                    verify(dummyIORepository, times(1)).processHeavyIO(eq(i))
                }
            }
        }

        @Test
        fun `Throws an Exception when DummyIORepository#processHeavyIO throws an Exception`() {
            dispatcher.runBlockingTest {
                doThrow(Exception("An irregular case"))
                    .whenever(dummyIORepository)
                    .processHeavyIO(anyInt())

                assertThrows<Exception> {
                    coroutineTestSample.fetchData(1)
                }

                verify(dummyIORepository, times(1)).processHeavyIO(anyInt())
            }
        }
    }
}
