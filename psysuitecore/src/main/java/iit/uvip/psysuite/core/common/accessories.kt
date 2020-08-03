package iit.uvip.psysuite.core.common

import android.content.Context
import android.os.Environment
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.albaspazio.core.accessory.VibrationManager
import org.albaspazio.core.accessory.getAbsoluteFilePath
import org.albaspazio.core.accessory.saveText
import java.util.*

@Parcelize
data class TaskCodeLabels(val label:String, val id:Int, val label_log:String = label) : Parcelable {
    override fun toString(): String {
        return label
    }
}

fun List<TaskCodeLabels>.getLabelLog(type:Int):String{
    this.map{
        if(it.id == type)  return it.label_log
    }
    return ""
}

fun List<TaskCodeLabels>.getLabel(type:Int):String{
    this.map{
        if(it.id == type)  return it.label
    }
    return ""
}

@Parcelize
data class TestResult(var code:Int=-1, var mailsubject:String, var mailbody:String, var res_files: ArrayList<String> = arrayListOf(), val testClass:String) :
    Parcelable

data class StimulusATBInfants(val type: Int, val tactile_pattern:Int)
data class Stimulus3delay(val type: Int, val a:Long, val t:Long, val v:Long)
data class StimulusBindingsUnbalanced(val type: Int, val delay:Long)
data class StimulusBIS(val ntrials:Int, val position:Int, val conflict:String)

fun VibrationManager.vibrateSingle(paramsT:TactileManager) {
    this.vibrateSingle(paramsT.duration, paramsT.amplitude)
}

abstract class Summary(private val ctx: Context){

    abstract fun add(trial:TrialBasic)
    abstract fun close(filename:String, dir:String = Environment.DIRECTORY_DOWNLOADS):String

    protected fun writeFile(summary:String, filename:String, dir:String = Environment.DIRECTORY_DOWNLOADS):String{
        return when(saveText(ctx, filename, summary, dir, true, notifyDm=true)){
            true    -> getAbsoluteFilePath(filename, dir).second
            false   -> ""
        }
    }

}