package org.albaspazio.psysuite.core.managers

import android.app.Activity
import java.io.File

interface IResultsManager {
    fun uploadResults(context: Activity?, files: List<File>): Boolean
    fun retryFailedUploads(context: Activity?): Boolean
    fun parseResultFile(file: File): Any?
    fun moveResultFile(source: File, destination: File): Boolean
    fun deleteResultFile(file: File): Boolean
    fun sendResultsViaEmail(recipients: List<String>, files: List<File>): Boolean
    fun batchUpload(context: Activity?, files: List<File>): Map<String, Any>
}
