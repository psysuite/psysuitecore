package iit.uvip.psysuite.core.tests.temporalbinding

import iit.uvip.psysuite.core.trials.TrialBasic


//                     trial_id    0-8      "none"
class TrialBindingsUnBalanced(id:Int=-1, type:Int=0, override var stim_value:Long=0L, correct_answer:Int=-1):
    TrialBasic(id, type, "", correct_answer=correct_answer) {

    companion object {
        @JvmStatic val LOG_HEADER = "id\ttype\tdelay\tanswer\tsuccess\telapsed\n"
    }

    // all class exported as string
    override fun toString():String {
        return "$id\t$type\t$stim_value\n"
    }

    // data exported to log file
    override fun Log():String {
        return "$id\t$type\t$stim_value\t$user_answer\t$success\t$elapsed\n"
    }

    override fun debugInfo():String{
        return "${super.debugInfo()}, delay=$stim_value"
    }

    override fun updateTrial(newvalue:Long){
        stim_value = newvalue
    }
}
