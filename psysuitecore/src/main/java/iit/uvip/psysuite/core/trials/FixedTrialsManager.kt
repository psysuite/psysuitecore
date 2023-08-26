package iit.uvip.psysuite.core.trials

import iit.uvip.psysuite.core.tests.TestBasic

// this class manage all classic tasks/conditions using predetermined trials sequence
// they are defined in the test's class and passed here with initTrials
class FixedTrialsManager(trials:MutableList<TrialBasic>):
    TrialsManager(TestBasic.TEST_TRMAN_FIXED, trials) {

    override fun getNewTrial(): TrialBasic {
        currTrial++
        return mTrial
    }
}