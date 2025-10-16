package com.vtex.ads.sdk

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DebugFunctionTest {

    @Test
    fun `NO_OP should not throw exceptions`() {
        val noOp = DebugFunctions.NO_OP
        
        // Should not throw any exceptions
        noOp("TestLabel", "Test message")
        noOp("", "")
        noOp("Label with spaces", "Message with special chars: !@#$%^&*()")
    }

    @Test
    fun `NO_OP should accept any parameters`() {
        val noOp = DebugFunctions.NO_OP
        
        // Should accept various parameter types
        noOp("label", "message")
        noOp("", "empty label")
        noOp("empty message", "")
    }

    @Test
    fun `custom debug function should work correctly`() {
        val entries = mutableListOf<Pair<String, String>>()
        val customFunction: DebugFunction = { label, message -> 
            entries += label to message 
        }
        
        customFunction("TestLabel", "Test message")
        customFunction("AnotherLabel", "Another message")
        
        assertEquals(2, entries.size)
        assertEquals("TestLabel" to "Test message", entries[0])
        assertEquals("AnotherLabel" to "Another message", entries[1])
    }
}
