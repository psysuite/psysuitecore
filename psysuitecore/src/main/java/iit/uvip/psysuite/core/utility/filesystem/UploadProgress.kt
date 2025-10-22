package iit.uvip.psysuite.core.utility.filesystem

/**
 * Data class representing upload progress for a result file
 */
data class UploadProgress(
    val fileItem: ResultFileItem,
    val status: UploadStatus,
    val progress: Int = 0,
    val errorMessage: String? = null
)

/**
 * Enum representing different upload states
 */
enum class UploadStatus {
    PENDING,        // Waiting to be uploaded
    UPLOADING,      // Currently being uploaded
    SUCCESS,        // Successfully uploaded
    ERROR,          // Upload failed
    DUPLICATE,      // Already exists on server
    SKIPPED         // Skipped due to user choice or conditions
}