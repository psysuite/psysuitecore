package iit.uvip.psysuite.core.common

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Handler
import android.view.View
import android.widget.ImageView

import org.albaspazio.core.accessory.VibrationManager

// these handlers have a null property in TestBasic
// in each subclass, their dependance (vibrator, ImageView) are validated in init.


abstract class StimulusManager(open val amplitude:Int, open val duration:Long, protected val handler: Handler){

    override fun toString():String{
        return "${StimulusManager::class.java.simpleName}, ampl=$amplitude, duration=$duration"
    }
    abstract fun deliver(dur:Any?=null)
    abstract fun stop()
    abstract fun getHandler():Any?
    abstract fun isValid():Boolean
}

class ToneManager(val type:Int= ToneGenerator.TONE_CDMA_ALERT_INCALL_LITE,
                  override val amplitude:Int= ToneGenerator.MAX_VOLUME,
                  override val duration:Long=-1L, handler: Handler)
                  :StimulusManager(amplitude, duration, handler){

    private var mToneGen = ToneGenerator(AudioManager.STREAM_SYSTEM, amplitude)

    override fun deliver(dur:Any?){
        val d = dur ?: duration
        mToneGen.startTone(type, (d as Long).toInt())
    }

    override fun getHandler():ToneGenerator{
        return mToneGen
    }

    override fun isValid(): Boolean {
        return (duration > 0)
    }

    override fun stop(){
        mToneGen.stopTone()
    }
}

class MediaPlayerManager(private val ctx: Context, val resource:String, override val amplitude:Int=100,
                         override val duration:Long=-1L, handler: Handler)
                         :StimulusManager(amplitude, duration, handler){

    companion object{

        @Throws(AudioResourceException::class)
        fun getAudioResource(ctx: Context, resname:String, volume:Float=1F, deftype:String = "raw"): MediaPlayer{
            val mp = MediaPlayer.create(ctx, ctx.resources.getIdentifier(resname, deftype, ctx.packageName)) ?: throw AudioResourceException(resname)
            mp.setVolume(volume, volume)
            return mp
        }

        // playback audioresource until its end
        @Throws(AudioResourceException::class, Exception::class)
        fun playbackAllAudioResource(ctx: Context, resource:String, volume: Float=1F, deftype:String = "raw", onEnd:()-> Unit = {}){

            try{
                val mediaPlayer = getAudioResource(ctx, resource, volume, deftype)
                mediaPlayer.setOnCompletionListener{
                    onEnd()
                    it.release()
                }
                mediaPlayer.start()
            }
            catch (e:Exception){ throw e }
        }
    }

    private var currMPAudio: MediaPlayer?    = null
    private var loadedResource:String       = ""

    init{
        if(resource.isNotEmpty())   loadResource(resource, amplitude.toFloat())
    }

    fun isLoaded(resource:String):Boolean = (resource == loadedResource && currMPAudio != null)

    @Throws(AudioResourceException::class, Exception::class)
    fun loadResource(resname: String, volume:Float=1F, deftype:String = "raw"):MediaPlayer{
        try{
            currMPAudio = getAudioResource(ctx, resname, volume, deftype)
            loadedResource = resname
            return currMPAudio as MediaPlayer
        }
        catch (e:Exception){
            throw e
        }
    }

    override fun deliver(dur:Any?){
        val d = dur ?: duration
        currMPAudio?.start()
        handler.postDelayed({ stop() }, d as Long)
    }

    override fun stop(){
        currMPAudio?.stop()
        currMPAudio?.prepare()
    }

    override fun getHandler(): MediaPlayer? {
        return currMPAudio
    }

    override fun isValid(): Boolean {
        return (duration > 0 && currMPAudio != null)
    }
}

class VisualManager(private val type:Int, private val imgV: ImageView, var drawResOn:Int=1, val drawResOff:Int=0,
                    override val duration:Long=-1L, handler: Handler)
                    :StimulusManager(0, duration, handler){

    override fun deliver(dur:Any?){
        val d = dur ?: duration
        if(type == TestBasic.STIM_TYPE_V1)  imgV.visibility = View.VISIBLE
        else                                imgV.setImageResource(drawResOn)
        handler.postDelayed({ stop() }, d as Long)
    }

    override fun stop(){
        if(type == TestBasic.STIM_TYPE_V1)  imgV.visibility = View.INVISIBLE
        else                                imgV.setImageResource(drawResOff)
    }

    override fun getHandler(): ImageView {
        return imgV
    }

    override fun isValid(): Boolean {
        return (duration > 0)
    }
}

class TactileManager(private val vibrator: VibrationManager, override val amplitude:Int=-1,
                     val timings:List<Int> = listOf(), val amplitudes:List<Int> = listOf(),
                     override val duration:Long=-1L, handler: Handler)
                     :StimulusManager(amplitude, duration, handler){

    companion object{

        fun validatePattern(pattern:String):List<Int>?{
            val timings:MutableList<Int> = mutableListOf()
            pattern.split(",").map{
                try {
                    timings.add(it.toInt())
                }
                catch(e:NumberFormatException){
                    return null
                }
            }
            return timings
        }
    }

    override fun deliver(dur:Any?){
        val d = dur ?: duration
        vibrator.vibrateSingle(d as Long, amplitude)
    }

    override fun stop(){
        vibrator.cancel()
    }

    override fun getHandler(): VibrationManager? {
        return vibrator
    }

    override fun isValid(): Boolean {
        return (duration > 0)
    }
}

class AudioResourceException(msg:String):Exception(msg)
class VibratorNotDefinedException(msg:String):Exception(msg)
class ImageViewDefinedException(msg:String):Exception(msg)