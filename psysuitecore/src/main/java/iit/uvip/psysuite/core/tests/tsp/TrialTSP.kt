package iit.uvip.psysuite.core.tests.tsp

import android.util.Log
import iit.uvip.psysuite.core.trials.TrialBasic
import kotlin.math.abs

/*
    user_answer:  is the error = (result - correct_answer)
    correct_answer: the onset of the target stimuli (in ms) ? ncues*current_isi
    stim_value: the actual trial pace (main_isi +- magnitude)
    succ: if abs(curr error) < abs(prev error), then success is true
 */


class TrialTSP (id:Int=-1, type:Int, label:String,
                override var magnitude:Float,
                val isi:Long,
                val isBefore: Boolean,
                val nCues:Int,
                val duration:Long,
                isADA:Boolean=false): TrialBasic(id, type, label, isADA=isADA) {

    companion object {
        @JvmStatic val LOG_HEADER = "id\tlabel\tisi\tonset\terror\tsuccess\telapsed\tmagnitude\n"
    }

    init {
        setupTrial(magnitude)
    }

    // 0 trial latency is calculated when delivering the first stimuli. correct answer is thus nCues x actual isi (isi +- magnitude)
    override fun setupTrial(newvalue: Float):Long{
        magnitude       = newvalue
        correct_answer  = (nCues*stim_value).toInt()
        return stim_value
    }

    override val stim_value:Long
        get() = if(isBefore )   isi - magnitude.toLong()
                else            isi + magnitude.toLong()

// if i want to randomize the isi  (magnitude can be zero. cannot be adaptive)
//        get() = if(isBefore )   (isi - isi*ISI_RND_MULT*random()).toLong()
//                else            (isi + isi*ISI_RND_MULT*random()).toLong()

    // success is true if the present error is smaller than the previous one
    // if first trial, success is always true
    // results is # ms of user answer
    override fun setResponse(result:Int, elapsedms:Long, prev_tr: TrialBasic?, extra_text:String) {
        user_answer         = (result - correct_answer)
        elapsed             = elapsedms
        prev_trial          = prev_tr

        success =   if(prev_tr!= null){
                        val succ = (abs(user_answer) <= abs(prev_tr.user_answer))

                        val delta = user_answer - prev_tr.user_answer
                        Log.d("TrialTTC", "--------------------------------------------")
                        Log.d("TrialTTC", "delta=$delta,  success=$succ, curr_error=$user_answer, prev_error=${prev_tr.user_answer}, ")
                        Log.d("TrialTTC", "@@@@ SET RESPONSE: magn=${magnitude}, isi=$isi, nCues=$nCues")
                        succ
                    }
                    else    true
        user_answer_extra   = extra_text
    }

    protected fun stimvalue2magnitude():Long{
        return  if(isBefore)  isi - stim_value
                else          stim_value - isi
    }

    // all class exported as string
    override fun toString():String{
        return "$id\t$label\t$stim_value\t$correct_answer\t$user_answer\t$success\t$elapsed\t$magnitude\n"
    }

    // data exported to log file
    //          "id\tlabel\tisi         \tonset         \terror\tsuccess\telapsed\tmagnitude\n"
    override fun Log():String{
        return "$id\t$label\t$stim_value\t$correct_answer\t$user_answer\t$success\t$elapsed\t$magnitude\n"
    }

    override fun debugInfo():String{
        return "${super.debugInfo()}, pos=$stim_value"
    }
}