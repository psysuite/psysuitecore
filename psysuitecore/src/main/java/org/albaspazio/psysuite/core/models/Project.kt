package org.albaspazio.psysuite.core.models

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean
) {
    fun validate(): Boolean {
        return id.isNotEmpty() && name.isNotEmpty()
    }
}
