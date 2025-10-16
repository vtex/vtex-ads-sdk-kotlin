package com.vtex.ads.sdk

import com.vtex.ads.sdk.models.Channel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VtexAdsClientDebugTest {

    @Test
    fun `should create client with debug configuration`() {
        val entries = mutableListOf<Pair<String, String>>()
        val debugFunction: DebugFunction = { label, message -> 
            entries += label to message 
        }
        
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_ALL),
            debugFunction = debugFunction
        )

        // Client should be created successfully
        assertEquals("test-user", client.getCurrentUserId())
        
        client.close()
    }

    @Test
    fun `should create client without debug configuration`() {
        val client = VtexAdsClient(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE
        )

        // Client should be created successfully with default debug settings
        assertEquals("test-user", client.getCurrentUserId())
        
        client.close()
    }

    @Test
    fun `should create client with static values and debug`() {
        val entries = mutableListOf<Pair<String, String>>()
        val debugFunction: DebugFunction = { label, message -> 
            entries += label to message 
        }
        
        val client = VtexAdsClient.createWithStaticValues(
            publisherId = "test-publisher",
            sessionId = "test-session",
            userId = "test-user",
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.ADS_LOAD),
            debugFunction = debugFunction
        )

        // Client should be created successfully
        assertEquals("test-user", client.getCurrentUserId())
        
        client.close()
    }

    @Test
    fun `should create client from config with debug`() {
        val entries = mutableListOf<Pair<String, String>>()
        val debugFunction: DebugFunction = { label, message -> 
            entries += label to message 
        }
        
        val config = VtexAdsClientConfig(
            publisherId = "test-publisher",
            sessionIdProvider = { "test-session" },
            userIdProvider = { "test-user" },
            channel = Channel.SITE,
            debug = debugOf(VtexAdsDebug.EVENTS_ALL, VtexAdsDebug.ADS_LOAD),
            debugFunction = debugFunction
        )
        
        val client = VtexAdsClient(config)

        // Client should be created successfully
        assertEquals("test-user", client.getCurrentUserId())
        
        client.close()
    }
}
