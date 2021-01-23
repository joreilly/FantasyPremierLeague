package com.surrus.common

import android.content.Context
import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.fantasypremierleague.di.appModule
import org.junit.Test
import org.koin.test.check.checkModules
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TestKoinGraph  {
    private val context = getApplicationContext<Context>()

    @Test
    fun `checking koin modules`() {
        initKoin {
            androidContext(context)
            modules(appModule)
        }.checkModules {  }
    }
}