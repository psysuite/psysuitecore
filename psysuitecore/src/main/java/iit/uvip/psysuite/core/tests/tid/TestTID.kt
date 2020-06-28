package iit.uvip.psysuite.core.tests.tid

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import iit.uvip.psysuite.core.R
import iit.uvip.psysuite.core.common.TaskCode
import iit.uvip.psysuite.core.common.TestBasic
import iit.uvip.psysuite.core.common.TrialBasic
import iit.uvip.psysuite.core.utility.QuestObject
import org.albaspazio.core.accessory.VibrationManager
import org.albaspazio.core.accessory.showToast

// type     : audio/vibro
// duration : ref:100 & test:[50-200] /  ref:2000 & test:[1000-4000]

// TRIAL:
//    FIRST_STIMULUS_DELAY=1500--------s1------delta1------s2-----ISI=1000ms-----s3------delta2-------s4-----QUESTION_DELAY=1500ms------domanda

class TestTID(ctx: Context,
              override val data: SubjectTIDParcel,
              private val vibrator: VibrationManager?
) : TestBasic(ctx, data)
{
    var LOG_TAG:String = TestTID::class.java.simpleName

    private lateinit var mQuest: QuestObject
    private var isUsingQuest:Boolean = false

    private var stimDuration:Long = 0L

    companion object {

        @JvmStatic var NUM_BLOCKS                   = 6

        @JvmStatic var NUM_TRIALS_X_BLOCK           = 48    //  == N_FIXED_LATENCIES x N_REP_X_LATENCY_X_BLOCK
        @JvmStatic val NUM_FIXED_LATENCIES          = 6
        @JvmStatic val NUM_REP_X_LATENCY_X_BLOCK    = 7     // MUST BE ODD !!!

        @JvmStatic val ISI:Long                     = 1000L  // interval between pair#1 and pair#2

        @JvmStatic val REF_STIM_DUR_SHORT:Long      = 100L
        @JvmStatic val REF_STIM_DUR_LONG:Long       = 2000L

        @JvmStatic val recipients:Array<String> = arrayOf(  "uvip.apptester@gmail.com",
                                                            "tonelli.alessia@gmail.com",
                                                            "nicola.domenici@iit.it") // "psysuite.uvip@gmail.com",

//        @JvmStatic val TEST_STIMULUS_DURATION_1_MIN = 50
//        @JvmStatic val TEST_STIMULUS_DURATION_1_MAX = 200
//
//        @JvmStatic val TEST_STIMULUS_DURATION_2_MIN = 1000
//        @JvmStatic val TEST_STIMULUS_DURATION_2_MAX = 4000

        @JvmStatic val STIMULUS_DURATION_AUDIO:Long     = 50L
        @JvmStatic val STIMULUS_DURATION_TACTILE:Long   = 50L
        @JvmStatic val QUESTION_DELAY:Long              = 50L   // interval between end of last stimulus and dialog onset
        @JvmStatic val FIRST_STIMULUS_DELAY:Long        = 1500L // ms to wait before sending the first trial

        @JvmStatic val STIMULUS_TYPE_AUDIO          = "A"
        @JvmStatic val STIMULUS_TYPE_TACTILE        = "T"

        fun getConditionsInfo(ctx: Context): List<TaskCode> {

            val label   = ctx.resources.getString(R.string.tid_label_short)
            val sts     = ctx.resources.getString(R.string.tid_rb_short_text)
            val stl     = ctx.resources.getString(R.string.tid_rb_long_text)

            return mutableListOf(
                TaskCode(label + "_" + STIMULUS_TYPE_AUDIO + "_" + sts    , TEST_TID_SHORT_AUDIO),
                TaskCode(label + "_" + STIMULUS_TYPE_TACTILE + "_" + sts  , TEST_TID_SHORT_TACTILE),
                TaskCode(label + "_" + STIMULUS_TYPE_AUDIO + "_" + stl    , TEST_TID_LONG_AUDIO),
                TaskCode(label + "_" + STIMULUS_TYPE_TACTILE + "_" + stl  , TEST_TID_LONG_TACTILE)
            )
        }

        fun getNextTrialModes():List<List<Int>>{
            return listOf(  listOf(TEST_NEXTTRIAL_ANSWER),
                            listOf(TEST_NEXTTRIAL_ANSWER),
                            listOf(TEST_NEXTTRIAL_ANSWER),
                            listOf(TEST_NEXTTRIAL_ANSWER)) //, TEST_NEXTTRIAL_VOICE_ANSWER, TEST_NEXTTRIAL_VOICE_NORMAL_ANSWER))
        }

        fun getEmailRecipients():Array<String>{
            return recipients
        }
    }

    private var mToneGen    = ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME)
    private var mTone       = ToneGenerator.TONE_CDMA_ALERT_INCALL_LITE

    private val shortLatencies:List<Long> = listOf(50, 70, 80, 130, 180, 200)
    private val longLatencies:List<Long> = listOf(1000, 1250, 1700, 2300, 3300, 4000)

    // =======================================================================================================================================

    init{

        if (NUM_REP_X_LATENCY_X_BLOCK % 2 != 0) Log.e("", "variable NUM_REP_X_LATENCY_X_BLOCK must be ")


        nextTrailModality   = data.nextTrailModality
        abortMode           = TEST_ABORT_TRIALEND       // abort @ trial end
        showTrialsID        = TEST_SHOWTRIALS_ALWAYS    // trial id always shown

        mQuestion           = ctx.resources.getString(R.string.tid_question_text)
        validAnswers        = mutableListOf(ctx.resources.getString(R.string.tid_rb1_text), ctx.resources.getString(R.string.tid_rb3_text))

        initTest()
    }

    override fun initTest(){

        stimDuration = STIMULUS_DURATION_AUDIO
        when(data.type){
            TEST_TID_SHORT_AUDIO, TEST_TID_LONG_AUDIO -> stimDuration = STIMULUS_DURATION_TACTILE
        }

        mQuest      = QuestObject()
        currTrial   = 0

        // set question & create mTrials list
        if(isUsingQuest){
                createQuestTrials(stimDuration)
                setNonRefDelta()
        }
        else    createConstantTrials(stimDuration)

        nTrials     = mTrials.size

        mTestLabel = ""
        getConditionsInfo(ctx).map {
            if (it.id == data.type) mTestLabel = it.label
        }
        if(mTestLabel.isEmpty())    showToast("Should not happen. given test code was not recognized", ctx)

        createResultFile(data, TrialTID.LOG_HEADER)
    }

    // a trial has this temporal line:
    //    FIRST_STIMULUS_DELAY=--1500--s1--delta1-s2-----ISI=1000ms-----s3------delta2-------s4-----QUESTION_DELAY=1500ms------domanda
    //                                  |           |                    |                    |
    // S1:          FIRST_STIMULUS_DELAY
    // S2:          FIRST_STIMULUS_DELAY + duration + mTrial.delta1
    // S3:          FIRST_STIMULUS_DELAY + duration + mTrial.delta1 + duration + ISI
    // S4:          FIRST_STIMULUS_DELAY + duration + mTrial.delta1 + duration + ISI + duration + mTrial.delta2
    // QUESTION:    FIRST_STIMULUS_DELAY + duration + mTrial.delta1 + duration + ISI + duration + mTrial.delta2 + duration + QUESTION_DELAY

    override fun show(trialid:Int, isRepeat:Boolean){

        mTrial      = mTrials[trialid]

        // S1
        mStimuliHandler.postDelayed({
            deliverStimulus(mTrial as TrialTID)
            testEvent.accept(EVENT_STIMULI_START)
        }, FIRST_STIMULUS_DELAY)

        // S2
        mStimuliHandler.postDelayed({
            deliverStimulus(mTrial as TrialTID)
        }, FIRST_STIMULUS_DELAY + stimDuration + (mTrial as TrialTID).delta1 )

        // S3
        mStimuliHandler.postDelayed({
            deliverStimulus(mTrial as TrialTID)
        }, FIRST_STIMULUS_DELAY + stimDuration + (mTrial as TrialTID).delta1 + stimDuration + ISI)

        // S4
        mStimuliHandler.postDelayed({
            deliverStimulus(mTrial as TrialTID)
        }, FIRST_STIMULUS_DELAY + stimDuration + (mTrial as TrialTID).delta1 + stimDuration + ISI + stimDuration + (mTrial as TrialTID).delta2)

        // send stimuli-end event
        mStimuliHandler.postDelayed({
            onTrialEnd()
        }, FIRST_STIMULUS_DELAY + stimDuration + (mTrial as TrialTID).delta1 + stimDuration + ISI + stimDuration + (mTrial as TrialTID).delta2 + stimDuration + QUESTION_DELAY)
    }

    override fun onTrialEnd() {

        when (nextTrailModality) {
            TEST_NEXTTRIAL_BUTTON               ->  testEvent.accept(EVENT_SHOW_NEXT_BUTTON)
            TEST_NEXTTRIAL_AUTO                 ->  testEvent.accept(EVENT_SHOW_1SECABORT)

            TEST_NEXTTRIAL_VOICE_ANSWER         ->  testEvent.accept(EVENT_GIVE_VOCAL_ANSWER)
            TEST_NEXTTRIAL_ANSWER               ->  testEvent.accept(EVENT_GIVE_ANSWER)
            TEST_NEXTTRIAL_VOICE_NORMAL_ANSWER -> {
                testEvent.accept(EVENT_GIVE_VOCAL_ANSWER)
                testEvent.accept(EVENT_GIVE_ANSWER)
            }
        }
    }

    override fun nextTrial(prev_result: String, elapsed: Int): Int {

        if(isUsingQuest){
            val newdelta: Float = mQuest.getNewValue((prev_result != ""))
            when((mTrials[currTrial+1] as TrialTID).ref_first) {
                true ->     (mTrials[currTrial+1] as TrialTID).delta2 = newdelta.toInt()
                else ->     (mTrials[currTrial+1] as TrialTID).delta1 = newdelta.toInt()
            }
        }
        return super.nextTrial(prev_result, elapsed)
    }

    private fun deliverStimulus(trial: TrialTID){

        when(trial.type) {
            TEST_TID_SHORT_AUDIO, TEST_TID_LONG_AUDIO       -> mToneGen.startTone(mTone, trial.duration)
            TEST_TID_SHORT_TACTILE, TEST_TID_LONG_TACTILE   -> vibrator?.vibrateSingle(trial.duration.toLong())
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // set question and create trials list
    private fun createConstantTrials(duration:Long){

        var ref_delta = REF_STIM_DUR_SHORT
        when(data.type) {
            TEST_TID_LONG_AUDIO, TEST_TID_LONG_TACTILE -> ref_delta = REF_STIM_DUR_LONG
        }

        for(b in 0 until NUM_BLOCKS){

            val block_trials:MutableList<TrialBasic> = mutableListOf()

            for(t in 0 until NUM_REP_X_LATENCY_X_BLOCK/2){
                for(l in 0 until NUM_FIXED_LATENCIES){
                    when(data.type) {
                        TEST_TID_SHORT_AUDIO, TEST_TID_SHORT_TACTILE    -> {
                            block_trials.add(TrialTID(-1, data.type, b, data.group, data.session,  ref_delta.toInt(), shortLatencies[l].toInt(), true, duration.toInt(), validAnswers))
                            block_trials.add(TrialTID(-1, data.type, b, data.group, data.session, shortLatencies[l].toInt(), ref_delta.toInt(),false, duration.toInt(), validAnswers))
                        }
                        TEST_TID_LONG_AUDIO, TEST_TID_LONG_TACTILE      -> {
                            block_trials.add(TrialTID(-1, data.type, b, data.group, data.session,  ref_delta.toInt(), longLatencies[l].toInt(), true, duration.toInt(), validAnswers))
                            block_trials.add(TrialTID(-1, data.type, b, data.group, data.session, longLatencies[l].toInt(), ref_delta.toInt(),false, duration.toInt(), validAnswers))
                        }
                    }
                }
            }
            block_trials.shuffle()
            mTrials.addAll(block_trials)
        }

        // set trial id according to its order in the list
        mTrials.mapIndexed { index, trial -> trial.id = (index + 1) }
    }

    private fun createQuestTrials(duration:Long){

        var ref_delta = REF_STIM_DUR_SHORT
        when(data.type) {
            TEST_TID_LONG_AUDIO, TEST_TID_LONG_TACTILE -> ref_delta = REF_STIM_DUR_LONG
        }

        for(b in 0 until NUM_BLOCKS){

            val block_trials:MutableList<TrialBasic> = mutableListOf()

            for(t in 0 until NUM_TRIALS_X_BLOCK /2){
                // TrialTID(id:Int=-1, val block:Int, val session:Int, type:Int, val modality:Int, val delta1:Int, val delta2:Int, val ref_first:Int, val duration:Int)
                block_trials.add(TrialTID(-1, data.type, b, data.group, data.session,  ref_delta.toInt(), -1, true, duration.toInt(), validAnswers))
                block_trials.add(TrialTID(-1, data.type, b, data.group, data.session, -1, ref_delta.toInt(),false, duration.toInt(), validAnswers))
            }
            block_trials.shuffle()
            mTrials.addAll(block_trials)
        }

        // set trial id according to its order in the list
        mTrials.mapIndexed { index, trial -> trial.id = (index + 1) }
    }

    private fun setNonRefDelta(){
        // set first trial's test delta
        val nonref_delta:Float = mQuest.getFirstValue()

        if((mTrials[0] as TrialTID).ref_first)  (mTrials[0] as TrialTID).delta2 = nonref_delta.toInt()
        else                                    (mTrials[0] as TrialTID).delta1 = nonref_delta.toInt()

        (mTrials[0] as TrialTID).correct_answer =   if((mTrials[0] as TrialTID).delta1 > (mTrials[0] as TrialTID).delta2)   validAnswers[0]
                                                    else                                                                    validAnswers[1]

    }
    // =====================================================================================
}

//
//    private fun vars2code(): Int {
//        return if (data.interval_type == 0) {
//            if (data.group == 0) TEST_TID_SHORT_AUDIO
//            else TEST_TID_SHORT_TACTILE
//        } else {
//            if (data.group == 0) TEST_TID_LONG_AUDIO
//            else TEST_TID_LONG_TACTILE
//        }
//    }

//    private fun code2vars(): Pair<Int, Int> {
//
//        when (data.type) {
//            TEST_TID_SHORT_AUDIO -> {
//                data.group = 0
//                data.interval_type = 0
//            }
//            TEST_TID_SHORT_TACTILE -> {
//                data.group = 1
//                data.interval_type = 0
//
//            }
//            TEST_TID_LONG_AUDIO -> {
//                data.group = 0
//                data.interval_type = 1
//
//            }
//            TEST_TID_LONG_TACTILE -> {
//                data.group = 1
//                data.interval_type = 1
//            }
//        }
//        return Pair(data.group, data.interval_type)
//    }