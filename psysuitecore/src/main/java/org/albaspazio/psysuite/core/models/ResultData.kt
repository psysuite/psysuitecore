package org.albaspazio.psysuite.core.models

data class ResultData(
    val id: String,
    val projectId: String,
    val timestamp: Long,
    val data: Map<String, Any>,
    val fileFormat: String // "JSON" or "TXT"
) {
    fun validate(): Boolean {
        return id.isNotEmpty() && projectId.isNotEmpty() && fileFormat in listOf("JSON", "TXT")
    }
}
