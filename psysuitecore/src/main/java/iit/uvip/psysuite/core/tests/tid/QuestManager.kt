package iit.uvip.psysuite.core.tests.tid

import com.chaquo.python.PyObject
import iit.uvip.psysuite.quest.QuestParams
import iit.uvip.psysuite.quest.QuestWrapper
import iit.uvip.psysuite.core.tests.TestBasic
import iit.uvip.psysuite.core.tests.TrialBasic
import iit.uvip.psysuite.core.tests.TrialsManager
import iit.uvip.psysuite.python.SPython

/*
 - it instanciates the python's task wrapper
 - get the first value and update the first trial
 */
class QuestManager(trials:MutableList<TrialBasic>, val wrapper: QuestWrapper, val params: QuestParams):TrialsManager(TestBasic.TEST_TRMAN_QUEST, trials) {

    val sPy:SPython = SPython.getInstance(null)

    private val wrapperModule:PyObject
    private val wrapperClass:PyObject

    init {
        if(mTrials.isEmpty())   throw Exception("ERROR in QuestTID. given trials list is empty")
        setTrialsID()

        wrapperModule       = sPy.getModule(wrapper.module);
        wrapperClass        = wrapperModule.callAttr(wrapper.classname, params, wrapper.params)

        val firstvalue      = wrapperClass.callAttr("get").toFloat()
        mTrial.updateTrial(firstvalue)
    }

    override fun setResponse(result: Int, elapsedms: Int, extra_text:String){
        mTrial.setResponse(result, elapsedms)
        wrapperClass.callAttr("set", result)
    }

    override fun getNewTrial():TrialBasic{
        val newvalue = wrapperClass.callAttr("get").toFloat()
        currTrial++
        mTrial.updateTrial(newvalue)
        return mTrial
    }
}