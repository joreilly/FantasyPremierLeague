package dev.johnoreilly.common.data.repository

import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Runs [block], retrying failures up to [attempts] times in total with a doubling delay between
 * tries, and rethrows the last failure. Cancellation is never retried.
 *
 * The app's first data load runs once at start-up, so a single transient failure (a dropped
 * connection, a slow cold network) would otherwise leave the players list spinning for good.
 */
internal suspend fun <T> retrying(
    attempts: Int = 3,
    initialDelay: Duration = 2.seconds,
    block: suspend () -> T,
): T {
    require(attempts >= 1)
    var delayBetween = initialDelay
    repeat(attempts - 1) {
        try {
            return block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            delay(delayBetween)
            delayBetween *= 2
        }
    }
    return block()
}
