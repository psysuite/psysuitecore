package org.albaspazio.psysuite.core.models

data class ApiResponse(
    val statusCode: Int,
    val headers: Map<String, String>,
    val body: String,
    val isSuccess: Boolean
) {
    fun validate(): Boolean {
        return statusCode > 0
    }
}
