package iit.uvip.psysuite.core.tests.bis

import iit.uvip.psysuite.core.trials.TrialBasic


class TrialBIS(id:Int=-1, type:Int, label:String, corr_answer:Int, override var stim_value:Long, val conflict_type:String, val duration:Long, private val duration2:Long=0L, private val mid_latency:Long = 500L): TrialBasic(id,type,label, correct_answer=corr_answer){

    companion object {
        @JvmStatic val LOG_HEADER = "id\tlabel\tlat\tconfl\tres\tcor_ans\tuser_ans\telapsed\trep\n"
    }

    init {
        updateTrial(stim_value.toFloat())
    }
    // all class exported as string
    override fun toString():String{
        return "$id\t$type\t$label\t$conflict_type\t$stim_value\t$duration\t$success\t$duration2\n"
    }

    // data exported to log file
    override fun Log():String{
        return "$id\t$label\t$stim_value\t$conflict_type\t$success\t$correct_answer\t$user_answer\t$elapsed\t$repetitions\n"
    }

    override fun debugInfo():String{
        return "${super.debugInfo()}, pos=$stim_value, conf_type=$conflict_type"
    }

    override fun updateTrial(newvalue:Float){
        stim_value      = newvalue.toLong()
        correct_answer  =   if(stim_value >= mid_latency)   1
                            else                            0
    }
}
