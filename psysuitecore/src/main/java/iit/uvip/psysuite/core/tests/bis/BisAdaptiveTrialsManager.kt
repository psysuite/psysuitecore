package iit.uvip.psysuite.core.tests.bis

import android.util.Log
import iit.uvip.psysuite.adaptive.AdaptiveWrapper
import iit.uvip.psysuite.core.trials.MixedAdaptiveTrialsManager
import iit.uvip.psysuite.core.trials.TrialBasic
import kotlin.random.Random

// BISECTION trial manager, convert adaptive value to milliseconds

class BisAdaptiveTrialsManager(trials:MutableList<TrialBasic>, adaptiveWrapper: AdaptiveWrapper, private val offset:Long):MixedAdaptiveTrialsManager(trials, adaptiveWrapper) {

    // I originally take the relative-to-midline values from static/adaptive schema and convert in absolute ms
    override fun getStimulus():Long{

        return  if(mTrial is TrialBISADA){
                    val magn = wrapperClass.callAttr("get").toFloat().toLong()
                    mTrial.updateTrial(magn)
                    (mTrial as TrialBISADA).magnitude2stimvalue()
                }
                else    mTrial.stim_value
    }

    override fun setResponse(result:Int, elapsedms:Int, extra_text:String){
        mTrial.setResponse(result, elapsedms)   // it updates mTrial.success

        if(mTrial is TrialBISADA)   wrapperClass.callAttr("set", mTrial.success, (mTrial as TrialBISADA).magnitude)
    }
}