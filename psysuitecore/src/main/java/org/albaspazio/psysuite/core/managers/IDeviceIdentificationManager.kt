package org.albaspazio.psysuite.core.managers

interface IDeviceIdentificationManager {
    fun generateDeviceId(): String
    fun getDeviceId(): String
    fun isDeviceRegistered(): Boolean
    fun setDeviceRegistered(registered: Boolean)
}
