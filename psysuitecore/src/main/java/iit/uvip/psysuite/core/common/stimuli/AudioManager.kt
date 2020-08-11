package iit.uvip.psysuite.core.common.stimuli

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Handler
import iit.uvip.psysuite.core.common.TestBasic

class AudioManager(type:Int, var resource:Any, override var amplitude:Int = -1, override val duration:Long=-1L, handler: Handler, private val ctx: Context)
    : StimulusManager(type, amplitude, duration, handler){

    private var mToneGen:ToneGenerator?     = null
    private var currMPAudio: MediaPlayer?   = null
    private var loadedResource:String       = ""

    companion object{

        @Throws(AudioResourceException::class)
        fun getAudioResource(ctx: Context, resname:String, volume:Float=1F, deftype:String = "raw"): MediaPlayer {
            val mp = MediaPlayer.create(ctx, ctx.resources.getIdentifier(resname, deftype, ctx.packageName)) ?: throw AudioResourceException(
                resname
            )
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
    init{
        if(type == TestBasic.STIM_TYPE_A1){
            if((resource as Int) == -1) resource  = ToneGenerator.TONE_CDMA_ALERT_INCALL_LITE
            if(amplitude == -1)         amplitude = ToneGenerator.MAX_VOLUME

            mToneGen = ToneGenerator(AudioManager.STREAM_SYSTEM, amplitude)
        }
        else{
            if((resource as String).isNotEmpty()){
                loadResource(resource as String, amplitude.toFloat())
                if(isValid()) currMPAudio?.dummyUse(amplitude.toFloat())
            }
        }
    }

    override fun deliver(dur:Any?){
        val d = dur ?: duration

        if(type == TestBasic.STIM_TYPE_A1)  mToneGen!!.startTone(resource as Int, (d as Long).toInt())
        else{
            currMPAudio?.start()
            handler.postDelayed({ stop() }, d as Long)
        }
    }

    override fun getHandler():Any? {
        return  if(type == TestBasic.STIM_TYPE_A1)  mToneGen!!
                else                                currMPAudio
    }

    override fun isValid(): Boolean {
        return (duration > 0)
    }

    override fun stop(){
        if(type == TestBasic.STIM_TYPE_A1)  mToneGen!!.stopTone()
        else{
            currMPAudio?.stop()
            currMPAudio?.prepare()
        }
    }

    fun isLoaded(resource:String):Boolean = (resource == loadedResource && currMPAudio != null)

    @Throws(AudioResourceException::class, Exception::class)
    fun loadResource(resname: String, volume:Float=1F, deftype:String = "raw"):MediaPlayer{
        try{
            currMPAudio     = getAudioResource(ctx, resname, volume, deftype)
            loadedResource  = resname
            return currMPAudio as MediaPlayer
        }
        catch (e:Exception){
            throw e
        }
    }
}