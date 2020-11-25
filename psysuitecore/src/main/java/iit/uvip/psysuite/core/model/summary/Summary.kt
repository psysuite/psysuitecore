package iit.uvip.psysuite.core.model.summary

import android.content.Context
import android.os.Environment
import iit.uvip.psysuite.core.tests.TrialBasic
import org.albaspazio.core.accessory.getAbsoluteFilePath
import org.albaspazio.core.accessory.saveText


abstract class Summary(private val ctx: Context){

    abstract var conditions: List<SummaryCondition>

    protected var summary:String = ""
    protected open val cond_labels:List<String> = listOf()

    // after each trial, filled (with response and success) trial is added to summary
    open fun add(trial: TrialBasic){
        conditions[0].add(trial)
    }

    // writes and return absolute filepath or empty
    open fun close(filename:String, dir:String = Environment.DIRECTORY_DOWNLOADS):String{

        if(conditions.size == 1) {
            conditions[0].close()
            summary = conditions[0].toString()
        }
        else {
            conditions.map {
                it.close()
                summary += "Condition ${it.label}\n"
                summary += it.toString()
            }
        }
        return writeFile(summary, filename, dir)
    }

    // return filename or empty
    private fun writeFile(summary:String, filename:String, dir:String = Environment.DIRECTORY_DOWNLOADS):String{
        return when(saveText(ctx, filename, summary, dir, true, notifyDm=true)){
            true    -> getAbsoluteFilePath(filename, dir).second
            false   -> ""
        }
    }

}