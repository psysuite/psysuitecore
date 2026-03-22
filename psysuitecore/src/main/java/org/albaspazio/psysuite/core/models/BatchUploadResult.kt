package org.albaspazio.psysuite.core.models

data class BatchUploadResult(
    val totalFiles: Int,
    val successfulUploads: Int,
    val failedUploads: Int,
    val errors: List<String>
) {
    fun validate(): Boolean {
        return totalFiles >= 0 && successfulUploads >= 0 && failedUploads >= 0
    }
}
