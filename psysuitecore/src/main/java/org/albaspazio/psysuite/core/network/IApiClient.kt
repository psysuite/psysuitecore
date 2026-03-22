package org.albaspazio.psysuite.core.network

interface IApiClient {
    fun makeRequest(request: Any): Any?
    fun isConnected(): Boolean
    fun setConnectionTimeout(timeoutMs: Long)
}
