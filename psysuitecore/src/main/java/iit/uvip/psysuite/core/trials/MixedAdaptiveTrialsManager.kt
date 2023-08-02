package iit.uvip.psysuite.core.trials

import iit.uvip.psysuite.adaptive.AdaptiveWrapper


// manage both adaptive and fixed trials

open class MixedAdaptiveTrialsManager(trials:MutableList<TrialBasic>, adaptiveWrapper:AdaptiveWrapper):AdaptiveTrialsManager(trials, adaptiveWrapper) {

    override fun getStimulus():Long{
        var newvalue = 0L
        return  if(mTrial.stim_value == ADAPTIVE_VALUE) {
                        newvalue = wrapperClass.callAttr("get").toFloat().toLong()
                        mTrial.updateTrial(newvalue)
                        newvalue
                }
                else    mTrial.stim_value
    }
}