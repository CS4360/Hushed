package com.example.hushed

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith


// Empty IT Test to hold package structure.  When writing actual IT tests discard this file
@RunWith(AndroidJUnit4::class)
class EmptyITTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.hushed", appContext.packageName)
    }
}