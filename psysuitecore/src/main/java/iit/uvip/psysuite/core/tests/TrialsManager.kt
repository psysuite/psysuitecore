package iit.uvip.psysuite.core.tests


/*
type:
- 0:    predetermined trials
- 1:    quest trials
- ?:    other type of adaptative algorithms
 */

abstract class TrialsManager(val type:Int = 0, val mTrials: MutableList<TrialBasic>) {

    var currTrial:Int = 0

    open var mTrial:TrialBasic
        get() = mTrials[currTrial]
        set(value) {
            mTrials[currTrial] = value
        }

    val nTrials:Int
        get() = mTrials.size

    init {
        if(mTrials.isEmpty())   throw Exception("ERROR in TrialsManager. given trials list is empty")
        setTrialsID()
    }

    open fun setResponse(result:Int, elapsedms:Int, extra_text:String = ""){
        mTrial.setResponse(result, elapsedms, extra_text)
    }

    abstract fun getNewTrial():Any

    protected fun setTrialsID(){  mTrials.mapIndexed { index, trialBasic -> trialBasic.id = index } }
}

// this class manage all classic tasks/conditions using predetermined trials sequence
// they are defined in the test's class and passed here with initTrials
class FixedTrialsManager(trials:MutableList<TrialBasic>):TrialsManager(TestBasic.TEST_TRMAN_FIXED, trials) {

    override fun getNewTrial():TrialBasic{
        currTrial++
        return mTrial
    }
}