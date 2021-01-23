package dev.johnoreilly.common

import co.touchlab.kermit.LogcatLogger
import co.touchlab.kermit.Logger


actual fun getLogger(): Logger = LogcatLogger()