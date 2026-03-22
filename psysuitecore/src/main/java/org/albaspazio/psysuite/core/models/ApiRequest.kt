package org.albaspazio.psysuite.core.models

data class ApiRequest(
    val endpoint: String,
    val method: String,
    val headers: Map<String, String>,
    val body: String?
) {
    fun validate(): Boolean {
        return endpoint.isNotEmpty() && method.isNotEmpty()
    }
}
