package org.example.demo.coroutine

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.Exception

@ExtendWith(MockKExtension::class)
@ExperimentalCoroutinesApi
internal class CoroutineTestSampleTestWithMockK {

    lateinit var coroutineTestSample: CoroutineTestSample

    @MockK
    lateinit var dummyIORepository: DummyIORepository

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        coroutineTestSample = CoroutineTestSample(
            dummyIORepository = dummyIORepository,
            coroutineDispatcher = dispatcher
        )
    }

    @AfterEach
    fun tearDown() {
        dispatcher.cleanupTestCoroutines()
        clearAllMocks()
    }

    @Nested
    inner class FetchDataTest {
        @Test
        fun `Returns the list of 1 to n in success cases`() {
            for (i in 1..10) {
                // Somehow coEvery function comes into an infinite loop in MockK 1.10.3
                // Upgrading MockK into 1.11.0 fixed the problem
                coEvery { dummyIORepository.processHeavyIO(i) } returns i
            }

            dispatcher.runBlockingTest {
                val expected = (1..10).toList()
                val actual = coroutineTestSample.fetchData(10)
                assertEquals(expected, actual)
            }

            for (i in 1..10) {
                coVerify(exactly = 1) {
                    dummyIORepository.processHeavyIO(i)
                }
            }
        }

        @Test
        fun `Throws an Exception when DummyIORepository#processHeavyIO throws an Exception`() {
            dispatcher.runBlockingTest {
                coEvery { dummyIORepository.processHeavyIO(any()) }
                    .throws(Exception("An irregular case"))

                assertThrows<Exception> {
                    coroutineTestSample.fetchData(1)
                }

                coVerify(exactly = 1) {
                    dummyIORepository.processHeavyIO(any())
                }
            }
        }
    }
}
