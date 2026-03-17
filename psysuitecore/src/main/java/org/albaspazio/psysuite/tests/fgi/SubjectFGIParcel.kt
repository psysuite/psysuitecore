package org.albaspazio.psysuite.tests.fgi

import org.albaspazio.psysuite.model.Populations
import org.albaspazio.psysuite.model.SubjectBasicParcel
import org.albaspazio.psysuite.stimuli.DelaysAligner
import org.albaspazio.psysuite.tests.TestBasic
import kotlinx.parcelize.Parcelize
import org.albaspazio.core.accessory.Device
import org.albaspazio.psysuite.R

// session
@Parcelize
class SubjectFGIParcel(

    override var classes: List<String> = listOf("org.albaspazio.psysuite.tests.fgi.TestFGI"),
    override var label: String = "",
    override var age: Int = -1,
    override var gender: Int = -1,
    override var population: Int = Populations.POPULATION_TD,
    override var type: Int = -1,
    override var project: String = "",

    override var block: Int = -1,
    override var isDebug: Boolean = false,
    override var device: Device? = null,
    override var vercode: Int = -1,
    override var stimuliDelays: DelaysAligner = DelaysAligner(),

    override var nextTrailModality: Int = TestBasic.TEST_NEXTTRIAL_BUTTON,
    override var whitenoise: Int = TestBasic.TEST_SWITCH_DISABLED,
    override var trman_type: Int = TestBasic.TEST_TRMAN_FIXED,
    override var showResult: Int = TestBasic.TEST_SWITCH_DISABLED,
    override var canRepeat:Int = TestBasic.TEST_SWITCH_DISABLED,
    override var doTraining: Int = TestBasic.TEST_SWITCH_DISABLED,

    override var showTrialID: Int = TestBasic.TEST_SHOWTRIALS_NEVER,
    override var abortMode: Int = TestBasic.TEST_ABORT_TRIALEND,

    override var session_spsel: Int = TestBasic.Companion.TEST_NO_LONGITUDINAL,
    override var session_spdatares: Int = R.array.sessions_array,
    override var date: String = "",
    override var exp_uid: String = ""
) : SubjectBasicParcel(classes, label, age, gender, population, type, project, block, isDebug, device, vercode, stimuliDelays, nextTrailModality, whitenoise, trman_type, showResult, canRepeat, doTraining, showTrialID, abortMode, session_spsel, session_spdatares, date, exp_uid)






