package org.albaspazio.psysuite.core.stimuli

import android.os.Handler
import android.os.VibrationEffect
import android.util.Log
import org.albaspazio.core.accessory.VibrationManager

class TactileManager(
    private val vibrator: VibrationManager,
    var amplitudes:IntArray = intArrayOf(),
    val timings:LongArray = longArrayOf(),
    override var duration:Long=-1L,
    val handler: Handler,
    override val type:Int = StimuliManager.STIM_TYPE_T1
): iStimulusManager{

    private lateinit var vibrationPattern:VibrationEffect

    override val isValid:Boolean
        get(){
            return  if(type == StimuliManager.STIM_TYPE_T1) duration > 0
                    else                                    timings.isNotEmpty() && amplitudes.isNotEmpty()
        }

    init {
        if(type == StimuliManager.STIM_TYPE_T1){
            duration = when(timings.size) {
                0 -> {
                    if(duration == -1L)  throw Exception("ERROR in TactileManager initialized as TYPE_T1, timings has lenght == 0 and duration was not set")
                    else                duration
                }
                1 -> timings[0]
                else -> throw Exception("ERROR in TactileManager initialized as TYPE_T1 but timings has lenght > 1")
            }

            amplitudes = when(amplitudes.size) {
                0 -> intArrayOf(-1)
                1 -> amplitudes
                else -> throw Exception("ERROR in TactileManager initialized as TYPE_T1 but amplitudes has lenght > 1")
            }
        } else {
            if(amplitudes.isEmpty() || timings.isEmpty())   throw Exception("ERROR in TactileManager initialized as TYPE_T2 but either amplitudes or timings has lenght == 0")
            if(amplitudes.size != timings.size)             throw Exception("ERROR in TactileManager initialized as TYPE_T2 but amplitudes and timings array have different lengths")

            duration = 0L
            timings.map {
                duration += it
            }
//            val timings: LongArray = longArrayOf(500, 500, 500, 500, 500, 500)
//            val amplitudes: IntArray = intArrayOf(255, 0, 255, 0, 255, 0)
            vibrationPattern = VibrationEffect.createWaveform(timings, amplitudes, -1)
        }
    }

    companion object{

        fun validateTimings(pattern:String):LongArray{
            val timings:MutableList<Long> = mutableListOf()
            if(pattern.isEmpty())   return longArrayOf()

            pattern.split(",").map{
                try {
                    timings.add(it.toLong())
                }
                catch(e:NumberFormatException){
                    throw Exception("Error in TactileManager.validateTimings")
                }
            }
            return timings.toLongArray()
        }
        fun validateAmplitudes(pattern:String):IntArray{
            val amplitudes:MutableList<Int> = mutableListOf()
            pattern.split(",").map{
                try {
                    amplitudes.add(it.toInt())
                }
                catch(e:NumberFormatException){
                    throw Exception("Error in TactileManager.validateAmplitudes")
                }
            }
            return amplitudes.toIntArray()
        }
    }

    override fun load(stim1: Any, stim2: Any?, clb: () -> Unit) {
        Log.w("TactileManager", "Method load not available")
    }

    override fun deliver(dur: Any?, id: Int){
        if(type == StimuliManager.STIM_TYPE_T1){
            val d = dur ?: duration
            vibrator.vibrateSingle(d as Long, amplitudes[0])
        }
        else{
            vibrator.vibratePattern(vibrationPattern)
        }
    }

    override fun stop(id: Int) {
        vibrator.cancel()
    }

    override fun getHandler(): VibrationManager {
        return vibrator
    }

    override fun clear() {}
}
