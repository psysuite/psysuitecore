package iit.uvip.psysuite.core.tests.bis

import iit.uvip.psysuite.core.trials.TrialBasic
import iit.uvip.psysuite.core.trials.TrialsManager.Companion.ADAPTIVE_VALUE

// trial adopting the pattern where magnitude and stim_value does not coincide....I fix a magnitude and, through the isBefore parameter, I calculate the stim_value

open class TrialBIS(id:Int=-1, type:Int, label:String, corr_answer:Int, var magnitude:Long, val isBefore:Boolean, val conflict_type:String, val duration:Long, private val duration2:Long=0L, val mid_latency:Long = 500L): TrialBasic(id,type,label, correct_answer=corr_answer){

    companion object {
        @JvmStatic val LOG_HEADER = "id\tlabel\tlat\tconfl\tres\tcor_ans\tuser_ans\telapsed\trep\n"
    }

    init {
        updateTrial(magnitude)
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

    final override fun updateTrial(newvalue:Long){
        magnitude       = newvalue

        if(magnitude != ADAPTIVE_VALUE)     stim_value      = magnitude2stimvalue()

        correct_answer  =   if(isBefore)    0
                            else            1
    }

    fun magnitude2stimvalue():Long{
        return  if(isBefore)    mid_latency - magnitude
                else            mid_latency + magnitude
    }

    protected fun stimvalue2magnitude():Long{
        return  if(isBefore)    mid_latency - stim_value
                else            stim_value - mid_latency
    }
}
