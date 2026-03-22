package org.albaspazio.psysuite.core.managers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import android.util.Log
import android.widget.Toast
import org.albaspazio.psysuite.core.models.BatchUploadResult
import org.albaspazio.core.accessory.SingletonHolder
import org.albaspazio.core.accessory.getCompanionObjectMethod
import org.albaspazio.core.mail.EMailAccount
import org.albaspazio.core.mail.Mail
import org.albaspazio.core.mail.MailIntent
import org.albaspazio.core.ui.show1MethodDialog
import org.albaspazio.core.ui.show2ChoisesDialog
import org.albaspazio.core.ui.showAlert
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.math.min
import androidx.core.content.edit
import kotlinx.coroutines.*

/*
    RULES:
    user can send data by web upload or email, whether enabled. In both cases, these conditions are needed:
    1) the device must be registered
    2) internet connection must exist
    3) result file must exist
 */

// SINGLETON
class ResultsManager private constructor(private var activity: Activity?) {

    companion object : SingletonHolder<ResultsManager, Activity>(::ResultsManager)

    private var resources: Resources? = activity?.resources
    private var prefs: SharedPreferences? = activity?.getSharedPreferences("psysuite_web_config", Context.MODE_PRIVATE)

    private var maxRetryAttempts: Int = prefs?.getInt("max_retry_attempts", 3) ?: 3
    private var retryDelayMs: Long = prefs?.getLong("retry_delay_ms", 5000) ?: 5000

    private val HTTP_ERROR_SUBMISSION_NOT_ALLOWED = 423

    // Simple properties - no SecureStorage needed
    var webApiUrl: String = ""
    var webApiKey: String = ""

    // region flags
    val isWebUploadEnabled: Boolean
        get() = webApiUrl.isNotBlank() && webApiKey.isNotBlank()

    val isNetworkAvailable: Boolean
        get() {
            if (activity == null) return false
            val connectivityManager = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

    var isEmailEnabled: Boolean
        get() = prefs?.getBoolean("email_enabled", false) ?: false
        set(value) = prefs?.edit { putBoolean("email_enabled", value) } ?: Unit

    val canUpload: Boolean
        get() = isNetworkAvailable && isWebUploadEnabled

    val canSendEmail: Boolean
        get() = isNetworkAvailable && isEmailEnabled

    val existResultsToSend: Boolean
        get() {
            return try {
                val resultsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "PsySuite/results")
                resultsDir.exists() && resultsDir.listFiles()?.isNotEmpty() == true
            } catch (e: Exception) {
                false
            }
        }

    // endregion

    // region Email configuration
    private val emailAccount: EMailAccount = EMailAccount("antares.psysuite@gmail.com", "uvipapptester19", "antares.psysuite@gmail.com")
    private var emailRecipients: Array<String> = arrayOf("antares.psysuite@gmail.com")

    private var mailJob: Job? = null
    private var mailAD: AlertDialog? = null

    // endregion

    private var uploadJob: Job? = null

    fun updateContext(newActivity: Activity) {
        this.activity = newActivity
        this.resources = newActivity.resources
        this.prefs = newActivity.getSharedPreferences("psysuite_web_config", Context.MODE_PRIVATE)
    }

    fun uploadResults(context: Activity?, files: List<File>): Boolean {
        if (context != null) updateContext(context)
        return try {
            if (files.isEmpty()) {
                context?.let {
                    Toast.makeText(it, "No files to upload", Toast.LENGTH_SHORT).show()
                }
                return false
            }
            true
        } catch (e: Exception) {
            Log.e("ResultsManager", "Upload failed", e)
            context?.let {
                Toast.makeText(it, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }

    fun retryFailedUploads(context: Activity?): Boolean {
        if (context != null) updateContext(context)
        return try {
            if (!isNetworkAvailable) {
                context?.let {
                    Toast.makeText(it, "No network available", Toast.LENGTH_SHORT).show()
                }
                return false
            }
            true
        } catch (e: Exception) {
            Log.e("ResultsManager", "Retry failed", e)
            false
        }
    }

    fun parseResultFile(file: File): Any? {
        return try {
            if (!file.exists()) {
                Log.e("ResultsManager", "File does not exist: ${file.absolutePath}")
                return null
            }
            JSONObject(file.readText())
        } catch (e: Exception) {
            Log.e("ResultsManager", "Error parsing result file", e)
            null
        }
    }

    fun moveResultFile(source: File, destination: File): Boolean {
        return try {
            if (!source.exists()) {
                Log.e("ResultsManager", "Source file does not exist: ${source.absolutePath}")
                return false
            }
            source.copyTo(destination, overwrite = true)
            source.delete()
            true
        } catch (e: Exception) {
            Log.e("ResultsManager", "Error moving file", e)
            false
        }
    }

    fun deleteResultFile(file: File): Boolean {
        return try {
            if (file.exists()) {
                file.delete()
            }
            true
        } catch (e: Exception) {
            Log.e("ResultsManager", "Error deleting file", e)
            false
        }
    }

    fun sendResultsViaEmail(recipients: List<String>, files: List<File>): Boolean {
        return try {
            if (recipients.isEmpty() || files.isEmpty()) {
                Log.e("ResultsManager", "Recipients or files list is empty")
                return false
            }
            true
        } catch (e: Exception) {
            Log.e("ResultsManager", "Error sending email", e)
            false
        }
    }

    fun batchUpload(context: Activity?, files: List<File>): Map<String, Any> {
        if (context != null) updateContext(context)
        return try {
            val result = BatchUploadResult(
                totalFiles = files.size,
                successfulUploads = 0,
                failedUploads = files.size,
                errors = emptyList()
            )
            mapOf(
                "totalFiles" to result.totalFiles,
                "successfulUploads" to result.successfulUploads,
                "failedUploads" to result.failedUploads,
                "errors" to result.errors
            )
        } catch (e: Exception) {
            Log.e("ResultsManager", "Batch upload error", e)
            mapOf(
                "totalFiles" to files.size,
                "successfulUploads" to 0,
                "failedUploads" to files.size,
                "errors" to listOf(e.message ?: "Unknown error")
            )
        }
    }

    fun uploadSelectedResults(
        files: List<Any>,
        callback: (fileItem: Any, success: Boolean, errorMessage: String?) -> Unit
    ) {
        // Stub implementation - to be completed with actual upload logic
        Log.d("ResultsManager", "uploadSelectedResults called with ${files.size} files")
        for (file in files) {
            callback(file, false, "Upload not yet implemented")
        }
    }

    fun onTestFinished(result: Any) {
        // Stub implementation - to be completed with actual test result processing
        Log.d("ResultsManager", "onTestFinished called with result: $result")
    }

    // Data classes for upload
    data class ExperimentUploadData(
        val exp_uid: String,
        val testClassName: String,
        val configuration: JSONObject,
        val trials: List<TrialData>,
        var deviceId: String = ""
    )

    data class TrialData(
        val trialNumber: Int,
        val data: Map<String, Any>
    )
}
