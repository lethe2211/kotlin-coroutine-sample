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

@ExperimentalCoroutinesApi
internal class DummyIORepositoryTest {

    lateinit var dummyIORepository: DummyIORepository
    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        dummyIORepository = DummyIORepositoryImpl(
            coroutineDispatcher = dispatcher
        )
    }

    @AfterEach
    fun tearDown() {
        dispatcher.cleanupTestCoroutines()
    }

    @Nested
    inner class ProcessHeavyIOTest {
        @Test
        fun `Return an integer in success cases`() {
            // TestCoroutineDispatcher#runBlockingTest will skip any delay functions
            // This test finishes without waiting for a long time
            dispatcher.runBlockingTest {
                val expected = 1
                val actual = dummyIORepository.processHeavyIO(1)
                assertEquals(expected, actual)
            }
        }
    }

    @Nested
    inner class ThrowExceptionTest {
        @Test
        fun `Throw an Exception in success cases`() {
            dispatcher.runBlockingTest {
                // Note: The below does not work if JUnit version is below 5.7
                assertThrows<Exception> {
                    dummyIORepository.throwException()
                }
            }
        }
    }
}
