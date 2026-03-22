package org.albaspazio.psysuite.core.managers

interface IProjectManager {
    fun createProject(project: Any): Boolean
    fun getProject(projectId: String): Any?
    fun getAllProjects(): List<Any>
    fun updateProject(project: Any): Boolean
    fun deleteProject(projectId: String): Boolean
}
