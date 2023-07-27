package iit.uvip.psysuite.core.trials

import android.util.Log
import iit.uvip.psysuite.adaptive.AdaptiveWrapper


// this class manages quest-driven tasks
// trials are defined in the test's class and passed here with their dynamic (subjected to quest) dimension set to zero
// user must pass a AdaptiveWrapper instance defining the python module/class governing the task.
// this class must have:
//      constructor with two parameters:
//          1- quest parameters
//          2- task parameters
//      two methods:
//          1- get = retrieve the dynamic value
//          2- set = set subject's answer
//
class MixedQuestTrialsManager(trials:MutableList<TrialBasic>, private val questWrapper: AdaptiveWrapper):QuestTrialsManager(trials, questWrapper) {

//
//    override fun setResponse(result: Int, elapsedms: Int, extra_text:String){
//        mTrial.setResponse(result, elapsedms)
//        wrapperClass.callAttr("set", result)
//    }

    init {
        setFirstStimulus()
    }


    private fun setFirstStimulus(){

//        if()
        val firstvalue      = wrapperClass.callAttr("get").toFloat()
        Log.d("QUEST_VALUE FIRST", firstvalue.toString())
        mTrial.updateTrial(firstvalue)
    }



    // get new value, get next trial, update with new value and return it
    override fun getNewTrial(): TrialBasic {
        val prev_resp = mTrial.user_answer
        currTrial++

        var newvalue = 0F
        if(mTrial.stim_value == ADAPTIVE_VALUE) {
            newvalue = wrapperClass.callAttr("get").toFloat()
            mTrial.updateTrial(newvalue)
        }
        else
            newvalue = mTrial.stim_value.toFloat()
        Log.d("QUEST_VALUE", "${newvalue} , prev resp: $prev_resp")

        return mTrial
    }
}