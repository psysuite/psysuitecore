package iit.uvip.psysuite.core.tests.tid

import iit.uvip.psysuite.core.trials.TrialBasic


class TrialTID(id:Int=-1, type:Int, val block:Int, val group:Int, val session:Int, val refdelta:Long, nonrefdelta:Long, val ref_first:Boolean, val duration:Long, val answers:List<String>): TrialBasic(id, type,"", -1){

    var delta1:Long = 0L
    var delta2:Long = 0L

    companion object {
        @JvmStatic val LOG_HEADER           = "id\ttype\tbl\tgrp\tses\tanswer\tsucc\telapsed\td1\td2\tref_first\n"
    }

    init {
        updateTrial(nonrefdelta)
    }

    override fun updateTrial(newvalue:Long){
        if(ref_first){
            delta1 = refdelta
            delta2 = newvalue
        }else{
            delta2 = refdelta
            delta1 = newvalue
        }

        // in quest mode, this assignment is wrong. correct_answer is updated when the new test value is calculated on-line
        correct_answer = when (delta2 > delta1) {
            true    -> 1
            false   -> 0
        }
    }

    override fun setResponse(result: Int, elapsedms: Int, extra_text:String) {
        super.setResponse(result, elapsedms, extra_text)
        user_answer = result
        elapsed     = elapsedms
        success     = (result == correct_answer)
    }

    // all class exported as string
    override fun toString():String{
        return "" //id.toString() + "\t" + type.toString() + "\t" + label + "\t" + conflict_type + "\t" + stim_value.toString() + "\t" + duration.toString() + "\t" + success.toString() + "\t" + duration2.toString()+ "\n"
    }

    // data exported to log file
    override fun Log(): String {
        return id.toString() + "\t" + type.toString() + "\t" + block.toString() + "\t" + group.toString() + "\t" + session.toString() + "\t" + user_answer + "\t" + success.toString() + "\t" + elapsed.toString() + "\t" + delta1.toString() + "\t" + delta2.toString() + "\t" + ref_first.toString() +"\n"
    }

    override fun debugInfo():String{
        return "${super.debugInfo()}, d1=$delta1, d2=$delta2, is_ref_first=$ref_first"
    }
}
