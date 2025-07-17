package com.surrus.common

import dev.johnoreilly.common.di.commonModule
import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.common.di.viewModelModule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TestKoinGraph  {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `checking koin modules`() {
        commonModule(false).verify()
    }
}