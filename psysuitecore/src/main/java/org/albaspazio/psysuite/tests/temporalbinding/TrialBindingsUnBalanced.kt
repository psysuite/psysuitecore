package org.albaspazio.psysuite.tests.temporalbinding

import org.albaspazio.psysuite.trials.TrialBasic
import org.albaspazio.psysuite.adaptive.ado.ADOWrapper


//                     trial_id    0-8      "none"
class TrialBindingsUnBalanced(id:Int=-1, type:Int=0, override var magnitude:Float, adoWrapper: ADOWrapper?=null):
    TrialBasic(id, type, adoWrapper =adoWrapper) {

    companion object {
        @JvmStatic val LOG_HEADER = "id\ttype\tdelay\tanswer\tsuccess\telapsed\n"
    }

    init {
        initTrial(magnitude)
    }

    // data exported to log file
    override fun Log():String {
        return "$id\t$type\t$stim_value\t$user_answer\t$success\t$elapsed\n"
    }

    override fun debugInfo():String{
        return "${super.debugInfo()}, delay=$stim_value"
    }

    override fun initTrial(newvalue: Float): Long {
        magnitude       = newvalue
        correct_answer  =   if(magnitude == 0.0F)   0
                            else                    1
        return stim_value
    }
}
