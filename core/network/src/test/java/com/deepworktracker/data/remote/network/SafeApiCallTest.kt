package com.deepworktracker.data.remote.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.UnknownHostException

class SafeApiCallTest {

    private val gson = Gson()

    @Test
    fun unknownHost_mapsToNoInternet() = runTest {
        val result = safeApiCall<String>(gson) { throw UnknownHostException("offline") }
        assertTrue(result is NetworkResult.NoInternet)
    }

    @Test
    fun jsonSyntax_mapsToParseError() = runTest {
        val result = safeApiCall<String>(gson) { throw JsonSyntaxException("bad") }
        assertTrue(result is NetworkResult.ParseError)
    }
}
