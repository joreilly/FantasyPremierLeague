package dev.johnoreilly.common.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.seconds

class RetryingTest {

    @Test
    fun `a transient failure is retried and the result returned`() = runTest {
        var calls = 0
        val result = retrying(attempts = 3, initialDelay = 2.seconds) {
            calls++
            if (calls < 3) error("transient") else "data"
        }
        assertEquals("data", result)
        assertEquals(3, calls)
        // Waited 2s then 4s between the three tries
        assertEquals(6_000, currentTime)
    }

    @Test
    fun `success first time does not wait`() = runTest {
        var calls = 0
        assertEquals("data", retrying { calls++; "data" })
        assertEquals(1, calls)
        assertEquals(0, currentTime)
    }

    @Test
    fun `the last failure is rethrown once attempts run out`() = runTest {
        var calls = 0
        val e = assertFailsWith<IllegalStateException> {
            retrying(attempts = 3) { calls++; error("down $calls") }
        }
        assertEquals("down 3", e.message)
        assertEquals(3, calls)
    }

    @Test
    fun `cancellation is not retried`() = runTest {
        var calls = 0
        assertFailsWith<CancellationException> {
            retrying(attempts = 3) { calls++; throw CancellationException("cancelled") }
        }
        assertEquals(1, calls)
    }
}
