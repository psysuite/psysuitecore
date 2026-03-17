package org.albaspazio.psysuite.tests.rivgrp

import android.content.Context
import org.albaspazio.psysuite.model.Populations
import org.albaspazio.psysuite.model.SubjectBasicParcel
import org.albaspazio.psysuite.stimuli.DelaysAligner
import org.albaspazio.psysuite.tests.TestBasic
import org.albaspazio.psysuite.tests.TestBasic.Companion.TEST_RIVGRP_RIVGRP_HC
import org.albaspazio.psysuite.tests.TestBasic.Companion.TEST_RIVGRP_RIVGRP_HF
import org.albaspazio.psysuite.utility.ConditionData
import org.albaspazio.psysuite.utility.getLabelLog
import kotlinx.parcelize.Parcelize
import org.albaspazio.core.accessory.Device
import org.albaspazio.core.accessory.getCompanionObjectMethod
import org.albaspazio.psysuite.R

// session
@Parcelize
class SubjectRIVGRPParcel(
    override var classes: List<String> = listOf("org.albaspazio.psysuite.tests.rivgrp.TestRIVGRP"),
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

    var rivFirst:Boolean        = true,
    var blockDuration:Long      = 150000,
    var minImagesXblock:Int     = 2,
    var defaultBlocks:List<Int> = listOf(2,2,4,2,2,4),
    var totBlocks:Int           = 4,

    override var session_spsel: Int = TestBasic.Companion.TEST_NO_LONGITUDINAL,
    override var session_spdatares: Int = R.array.sessions_array,
    override var date: String = "",
    override var exp_uid: String = ""

) : SubjectBasicParcel(classes, label, age, gender, population, type, project, block, isDebug, device, vercode, stimuliDelays, nextTrailModality, whitenoise, trman_type, showResult, canRepeat, doTraining, showTrialID, abortMode, session_spsel, session_spdatares, date, exp_uid){

    override fun getFilesPrefix(ctx:Context):String{

        val ci          = getCompanionObjectMethod(classes[0], "getConditionsInfo")
        val type_label  = (ci.first?.call(ci.second, ctx) as List<ConditionData>).getLabelLog(type)

        val first =     if(type == TEST_RIVGRP_RIVGRP_HF || type == TEST_RIVGRP_RIVGRP_HC) {
                            if (rivFirst)   "riv"
                            else            "grp"
                        }else               ""

        return "${label}_${population}_${first}_${totBlocks}_${blockDuration}_$type_label"
    }
}












