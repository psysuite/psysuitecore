package iit.uvip.psysuite.core.tests.bis

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import androidx.fragment.app.Fragment
import iit.uvip.psysuite.adaptive.AdaptiveWrapper
import iit.uvip.psysuite.adaptive.ado.ADOParams
import iit.uvip.psysuite.core.R
import iit.uvip.psysuite.core.model.Populations
import iit.uvip.psysuite.core.model.parcel.SubjectBasicParcel
import iit.uvip.psysuite.core.stimuli.*
import iit.uvip.psysuite.core.tests.TestBasic
import iit.uvip.psysuite.core.trials.TrialBasic
import iit.uvip.psysuite.core.trials.FixedTrialsManager
import iit.uvip.psysuite.core.trials.MixedQuestTrialsManager
import iit.uvip.psysuite.core.trials.TrialsManager
import iit.uvip.psysuite.core.utility.ConditionData
import iit.uvip.psysuite.core.utility.StimuliSetBIS
import org.albaspazio.core.accessory.VibrationManager
import org.albaspazio.core.speech.SpeechManager
import org.albaspazio.core.ui.showToast

class TestBIS(
    ctx: Context,
    activity: Activity,
    hostfragment: Fragment,
    subject: SubjectBasicParcel,
    vibrator: VibrationManager?,
    mImageView: ImageView?,
    speechManager:SpeechManager?
) : TestBasic(ctx, activity, hostfragment, subject, vibrator, mImageView, speechManager){

    override var LOG_TAG:String = TestBIS::class.java.simpleName

    companion object {

        @JvmStatic val TEST_BASIC_LABEL                 = "BIS"

        @JvmStatic var NUM_TRIALS                       = 32
        @JvmStatic val STIMULUS_DURATION_VISUAL:Long    = 50
        @JvmStatic val STIMULUS_DURATION_TACTILE:Long   = 50
        @JvmStatic val STIMULUS_DURATION_AUDIO:Long     = 50
        @JvmStatic val QUESTION_DELAY                   = 1500      // latency
        @JvmStatic val FIRST_STIMULUS_DELAY             = 1000L      // ms to wait before sending the first trial
        @JvmStatic val LAST_STIMULUS_DELAY              = 1000      // ms of the third stimulus wrt first

        @JvmStatic val TRIAL_STAGE_1                    = 1
        @JvmStatic val TRIAL_STAGE_2                    = 2
        @JvmStatic val TRIAL_STAGE_3                    = 3

        @JvmStatic val AV_STIMULUS_DELTA                = 200       // ms between the AV stimuli

        @JvmStatic val STIMULUS_TYPE_AUDIO              = "AUDIO"
        @JvmStatic val STIMULUS_TYPE_TACTILE            = "TACTILE"
        @JvmStatic val STIMULUS_TYPE_AUDIO_TACTILE      = "AUDIO_TACTILE"
        @JvmStatic val STIMULUS_TYPE_AUDIO_VIDEO        = "AUDIO_VIDEO"

        @JvmStatic val STIMULUS_TYPE_AUDIO_LOG          = "A"
        @JvmStatic val STIMULUS_TYPE_TACTILE_LOG        = "T"
        @JvmStatic val STIMULUS_TYPE_AUDIO_TACTILE_LOG  = "AT"
        @JvmStatic val STIMULUS_TYPE_AUDIO_VIDEO_LOG    = "AV"
        @JvmStatic val STIMULUS_TYPE_VIDEO_AUDIO_LOG    = "VA"

        @JvmStatic val CONFLICT_TYPE_NONE               = "none"

        fun getConditionsInfo(ctx: Context): List<ConditionData>{
            return if(VibrationManager.sysHasVibrator(ctx))
                mutableListOf(
                    ConditionData(TEST_BASIC_LABEL + "_" + STIMULUS_TYPE_AUDIO           , TEST_BISECTION_AUDIO          , "${TEST_BASIC_LABEL}$STIMULUS_TYPE_AUDIO_LOG"           , Populations.hearing_populations),
                    ConditionData(TEST_BASIC_LABEL + "_" + STIMULUS_TYPE_TACTILE         , TEST_BISECTION_TACTILE        , "${TEST_BASIC_LABEL}$STIMULUS_TYPE_TACTILE_LOG"         , Populations.all_populations),
                    ConditionData(TEST_BASIC_LABEL + "_" + STIMULUS_TYPE_AUDIO_TACTILE   , TEST_BISECTION_AUDIO_TACTILE  , "${TEST_BASIC_LABEL}$STIMULUS_TYPE_AUDIO_TACTILE_LOG"   , Populations.hearing_populations),
                    ConditionData(TEST_BASIC_LABEL + "_" + STIMULUS_TYPE_AUDIO_VIDEO     , TEST_BISECTION_AUDIO_VIDEO    , "${TEST_BASIC_LABEL}$STIMULUS_TYPE_AUDIO_VIDEO_LOG"     , Populations.sighted_hearing_populations))
            else
                mutableListOf(
                    ConditionData(TEST_BASIC_LABEL + "_" + STIMULUS_TYPE_AUDIO           , TEST_BISECTION_AUDIO          , "${TEST_BASIC_LABEL}$STIMULUS_TYPE_AUDIO_LOG"           , Populations.hearing_populations),
                    ConditionData(TEST_BASIC_LABEL + "_" + STIMULUS_TYPE_AUDIO_VIDEO     , TEST_BISECTION_AUDIO_VIDEO    , "${TEST_BASIC_LABEL}$STIMULUS_TYPE_AUDIO_VIDEO_LOG"     , Populations.sighted_hearing_populations))
        }
        fun getNextTrialModes(ctx:Context):List<List<Int>> = listOf(listOf(TEST_NEXTTRIAL_ANSWER)) //, TEST_NEXTTRIAL_VOICE_ANSWER, TEST_NEXTTRIAL_VOICE_NORMAL_ANSWER))
    }

    // contains : stimulus type & delay
    private var trialsDefaultSchema:List<StimuliSetBIS> = listOf(
        StimuliSetBIS(4, 200, CONFLICT_TYPE_NONE),
        StimuliSetBIS(6, 300, CONFLICT_TYPE_NONE),
        StimuliSetBIS(6, 400, CONFLICT_TYPE_NONE),
        StimuliSetBIS(6, 600, CONFLICT_TYPE_NONE),
        StimuliSetBIS(6, 700, CONFLICT_TYPE_NONE),
        StimuliSetBIS(4, 800, CONFLICT_TYPE_NONE)
    )

    // first stim is delivered at the given latency. the second  AV_STIMULUS_DELTA after
                                                                        // ntrials  latency conflict-type
    private var trialsAudioVideoSchema:List<StimuliSetBIS> = listOf(
        StimuliSetBIS(4, 200, STIMULUS_TYPE_VIDEO_AUDIO_LOG),
        StimuliSetBIS(4, 300, STIMULUS_TYPE_VIDEO_AUDIO_LOG),
        StimuliSetBIS(4, 400, STIMULUS_TYPE_VIDEO_AUDIO_LOG),
        StimuliSetBIS(4, 500, STIMULUS_TYPE_VIDEO_AUDIO_LOG),
        StimuliSetBIS(4, 600, STIMULUS_TYPE_VIDEO_AUDIO_LOG),
        StimuliSetBIS(4, 200, STIMULUS_TYPE_AUDIO_VIDEO_LOG),
        StimuliSetBIS(4, 300, STIMULUS_TYPE_AUDIO_VIDEO_LOG),
        StimuliSetBIS(4, 400, STIMULUS_TYPE_AUDIO_VIDEO_LOG),
        StimuliSetBIS(4, 500, STIMULUS_TYPE_AUDIO_VIDEO_LOG),
        StimuliSetBIS(4, 600, STIMULUS_TYPE_AUDIO_VIDEO_LOG)
    )

    private var STIM_A  = StimuliManager.STIM_TYPE_A4
    private var STIM_V  = StimuliManager.STIM_TYPE_V2
    private var STIM_T  = StimuliManager.STIM_TYPE_T1

    private var STIM_AV = STIM_A or STIM_V
    private var STIM_AT = STIM_A or STIM_T

    private var conflictType:String = ""
    private var currStimulus:String = ""
    private var currStimulusDuration2:Long     = 0L          // default value to be used when second stimulus duration in not given

    override var mDrawablesResource: MutableList<Int> = mutableListOf(R.drawable.white_circle, R.drawable.red_circle, R.drawable.grey_circle, R.drawable.blue_circle)

    private val nQuestTrials                = 3
    private val adoParams                   = ADOParams(guess_rate=0.5F, lapse_rate=0.04F, noise_perc=0.1F)
    private val taskAdoParams               = BisectionADOParams(400, 500)
    private val adoWrapper:AdaptiveWrapper  = AdaptiveWrapper("bisection.BisectionADOPYWrapper", "BisectionADOPYWrapper", adoParams, taskAdoParams)

//    private val questParams2     = QuestParams2AFC(tGuess=0.0F, noiseperc=0.1F, tGuessSd=1.5F, stepperc=0.4F, gamma=0.01F)
//    private val taskQuestParams2 = BisectionADOParams(600, 200)
//    private val questWrapper2:QuestWrapper = QuestWrapper("bisection.BisectionWrapper2", "BisectionWrapper2", questParams2, taskQuestParams2)

    // =============================================================================================================================
    // INIT
    // =============================================================================================================================
    override fun initTest() {
        // set stimuli default & create mTrials list
        when {
            mImageView == null -> throw ImageViewDefinedException("IMAGE_VIEW_NOT_DEFINED")
            vibrator == null && (subject.type == TEST_BISECTION_TACTILE || subject.type == TEST_BISECTION_AUDIO_TACTILE) -> throw VibratorNotDefinedException("VIBRATOR_NOT_DEFINED")
        }
        validAnswers = mutableListOf(ctx.resources.getString(R.string.bisection_rb1_text), ctx.resources.getString(R.string.bisection_rb3_text))

        // set mQuestion/ currStimulusDuration/ currStimulusDuration2/ currStimulusLabel
        when (subject.type) {
            TEST_BISECTION_AUDIO            -> initBisectionAudio()
            TEST_BISECTION_TACTILE          -> initBisectionTactile()
            TEST_BISECTION_AUDIO_TACTILE    -> initBisectionAudioTactile()
            else                            -> initBisectionAudioVideo()
        }

        mTrialsManager =
            when (subject.trman_type) {
                TEST_TRMAN_FIXED -> {
                    val trials = if (!subject.isDebug)
                                    when (subject.type) {
                                        TEST_BISECTION_AUDIO, TEST_BISECTION_TACTILE, TEST_BISECTION_AUDIO_TACTILE
                                                -> createDefaultTrials(currStimulusLabel, currStimulusDuration, currStimulusDuration2)
                                        else    -> createAudioVideoTrials(currStimulusDuration, currStimulusDuration2)
                                    }
                                 else createTrialsDebug()

                    FixedTrialsManager(trials as MutableList<TrialBasic>)
                }
                else -> {
                    val trials = createTrialsQuest()
                    MixedQuestTrialsManager(trials as MutableList<TrialBasic>, adoWrapper)
                }
            }

        mTestLabel = ""
        getConditionsInfo(ctx).map {
            if (it.id == subject.type) mTestLabel = it.label
        }
        if(mTestLabel.isEmpty()) showToast("Should not happen. given test code was not recognized", ctx)
        createResultFile(subject, TrialBIS.LOG_HEADER)

        mNoise = AudioManager.getAudioResource(ctx,"wnoise_20s", 0.01f)

        mStimuliManager =   if(vibrator != null)
                                StimuliManager(
                                    AudioManager(STIM_A, audioResources[STIMULUS_DURATION_AUDIO] ?: "t1000hz_50ms.wav",  duration = STIMULUS_DURATION_AUDIO, handler = mStimuliHandler, ctx = ctx),
                                    TactileManager(vibrator, duration = STIMULUS_DURATION_TACTILE, handler = mStimuliHandler),
                                    VisualManager(STIM_V, mImageView!!, mDrawablesResource[1], mDrawablesResource[0], duration = STIMULUS_DURATION_VISUAL, handler = mStimuliHandler),
                                    delaysAligner, ctx, mStimuliHandler)
                            else
                                StimuliManager(
                                    AudioManager(STIM_A, audioResources[STIMULUS_DURATION_AUDIO] ?: "t1000hz_50ms.wav",  duration = STIMULUS_DURATION_AUDIO, handler = mStimuliHandler, ctx = ctx),
                                    null,
                                    VisualManager(STIM_V, mImageView!!, mDrawablesResource[1], mDrawablesResource[0], duration = STIMULUS_DURATION_VISUAL, handler = mStimuliHandler),
                                    delaysAligner, ctx, mStimuliHandler)

        testEvent.accept(Pair(EVENT_TEST_SETUP_COMPLETED, null))
    }
    // =============================================================================================================================
    // CREATE TRIALS
    // =============================================================================================================================
    private fun createDefaultTrials(stim_type_label:String, duration:Long, duration2:Long=0L):List<TrialBasic>{

        val trials:MutableList<TrialBasic> = mutableListOf()
        for(section in trialsDefaultSchema)
            for(i in 0 until section.ntrials)
                //                      id   type       label,          corr_answ, stim_value          conflict_type     duration  duration2
                trials.add(TrialBIS(-1, subject.type, stim_type_label, 0, section.stim_value, section.conflict, duration, duration2))

        trials.shuffle()
        return trials
    }

    private fun createTrialsQuest():List<TrialBasic>{
        var cnt = -1
        val trials: MutableList<TrialBasic> = mutableListOf()
        for (i in 0 until nQuestTrials)
            trials.add(TrialBIS(++cnt, subject.type, STIMULUS_TYPE_AUDIO, 0,TrialsManager.ADAPTIVE_VALUE,CONFLICT_TYPE_NONE,STIMULUS_DURATION_AUDIO))

        trials.add(TrialBIS(-1, subject.type, currStimulusLabel, 0, 200, conflictType, currStimulusDuration, currStimulusDuration2))
        trials.add(TrialBIS(-1, subject.type, currStimulusLabel, 0, 300, conflictType, currStimulusDuration, currStimulusDuration2))
        trials.add(TrialBIS(-1, subject.type, currStimulusLabel, 0, 400, conflictType, currStimulusDuration, currStimulusDuration2))
        trials.add(TrialBIS(-1, subject.type, currStimulusLabel, 0, 450, conflictType, currStimulusDuration, currStimulusDuration2))
        trials.add(TrialBIS(-1, subject.type, currStimulusLabel, 0, 500, conflictType, currStimulusDuration, currStimulusDuration2))
        trials.add(TrialBIS(-1, subject.type, currStimulusLabel, 0, 550, conflictType, currStimulusDuration, currStimulusDuration2))
        trials.add(TrialBIS(-1, subject.type, currStimulusLabel, 0, 600, conflictType, currStimulusDuration, currStimulusDuration2))
        trials.add(TrialBIS(-1, subject.type, currStimulusLabel, 0, 700, conflictType, currStimulusDuration, currStimulusDuration2))
        trials.add(TrialBIS(-1, subject.type, currStimulusLabel, 0, 800, conflictType, currStimulusDuration, currStimulusDuration2))

        trials.shuffle()
        return trials
    }

    private fun createAudioVideoTrials(durationAudio:Long, durationVideo:Long):List<TrialBasic>{

        val trials:MutableList<TrialBasic> = mutableListOf()
        for(section in trialsAudioVideoSchema)
            for(i in 0 until section.ntrials){
                when(section.conflict == STIMULUS_TYPE_AUDIO_VIDEO_LOG){
                    //                                 id   type        label,                   corr_answ, stim_value          conflict_type   duration       duration2
                    true    -> trials.add(TrialBIS(-1, subject.type, STIMULUS_TYPE_AUDIO_VIDEO, 0, section.stim_value, section.conflict, durationAudio, durationVideo))
                    false   -> trials.add(TrialBIS(-1, subject.type, STIMULUS_TYPE_AUDIO_VIDEO, 0, section.stim_value, section.conflict, durationVideo, durationAudio))
                }
            }
        trials.shuffle()
        return trials
    }

    // ----------------------------------
    private fun initBisectionAudio(){
        mQuestion               = ctx.resources.getString(R.string.bisection_question_text_audio)
        currStimulusDuration    = STIMULUS_DURATION_AUDIO
        currStimulusLabel       = STIMULUS_TYPE_AUDIO
        conflictType            = CONFLICT_TYPE_NONE
    }

    private fun initBisectionTactile(){
        mQuestion               = ctx.resources.getString(R.string.bisection_question_text_tactile)
        currStimulusDuration    = STIMULUS_DURATION_TACTILE
        currStimulusLabel       = STIMULUS_TYPE_TACTILE
        conflictType            = CONFLICT_TYPE_NONE
    }

    private fun initBisectionAudioTactile(){
        mQuestion               = ctx.resources.getString(R.string.bisection_question_text_mixed)
        currStimulusDuration    = STIMULUS_DURATION_AUDIO
        currStimulusDuration2   = STIMULUS_DURATION_TACTILE
        currStimulusLabel       = STIMULUS_TYPE_AUDIO_TACTILE
        conflictType            = CONFLICT_TYPE_NONE
    }

    private fun initBisectionAudioVideo(){
        mQuestion               = ctx.resources.getString(R.string.bisection_question_text_mixed)
        currStimulusDuration    = STIMULUS_DURATION_AUDIO
        currStimulusDuration2   = STIMULUS_DURATION_VISUAL
        currStimulusLabel       = STIMULUS_TYPE_AUDIO_VIDEO
        conflictType            = CONFLICT_TYPE_NONE
    }

    private fun createTrialsDebug():List<TrialBasic>{
        mQuestion = ctx.resources.getString(R.string.bisection_question_text_mixed)

        val trials:MutableList<TrialBasic> = mutableListOf()
        for(i in 0 until 10000){
                //                     id   type                        label,                        corr_answ, stim_value          conflict_type   duration       duration2
            trials.add(TrialBIS(-1, TEST_BISECTION_AUDIO_TACTILE, STIMULUS_TYPE_AUDIO_TACTILE, 0, 100, CONFLICT_TYPE_NONE, STIMULUS_DURATION_AUDIO, STIMULUS_DURATION_TACTILE))
            trials.add(TrialBIS(-1, TEST_BISECTION_AUDIO_TACTILE, STIMULUS_TYPE_AUDIO_TACTILE, 0, 900, CONFLICT_TYPE_NONE, STIMULUS_DURATION_AUDIO, STIMULUS_DURATION_TACTILE))

            trials.add(TrialBIS(-1, TEST_BISECTION_AUDIO_VIDEO, STIMULUS_TYPE_AUDIO_VIDEO, 0, 100, STIMULUS_TYPE_VIDEO_AUDIO_LOG, STIMULUS_DURATION_AUDIO, STIMULUS_DURATION_VISUAL))
            trials.add(TrialBIS(-1, TEST_BISECTION_AUDIO_VIDEO, STIMULUS_TYPE_AUDIO_VIDEO, 0, 900, STIMULUS_TYPE_VIDEO_AUDIO_LOG, STIMULUS_DURATION_AUDIO, STIMULUS_DURATION_VISUAL))
            trials.add(TrialBIS(-1, TEST_BISECTION_AUDIO_VIDEO, STIMULUS_TYPE_AUDIO_VIDEO, 0, 100, STIMULUS_TYPE_AUDIO_VIDEO_LOG, STIMULUS_DURATION_VISUAL, STIMULUS_DURATION_AUDIO))
            trials.add(TrialBIS(-1, TEST_BISECTION_AUDIO_VIDEO, STIMULUS_TYPE_AUDIO_VIDEO, 0, 900, STIMULUS_TYPE_AUDIO_VIDEO_LOG, STIMULUS_DURATION_VISUAL, STIMULUS_DURATION_AUDIO))
        }
        return trials
    }
    // =============================================================================================================================
    // MANAGE TRIALS STIMULI
    // =============================================================================================================================
    override fun onTrialEnd(){

        mNoise?.stop()
        mNoise?.prepare()

        testEvent.accept(Pair(EVENT_GIVE_ANSWER, null))
    }

    override fun initSummary(){}

    // =============================================================================================================================
    // DELIVER STIMULI
    // =============================================================================================================================

    // a trial has this temporal line:
    // +  FIRST_STIMULUS_DELAY                          => 1st stim
    // + (FIRST_STIMULUS_DELAY + mTrial.stim_value)       => 2nd stim
    // + (FIRST_STIMULUS_DELAY + LAST_STIMULUS_DELAY)   => 3rd stim
    // + (QUESTION_DELAY + FIRST_STIMULUS_DELAY)        => event : show question
    override fun show(trial: TrialBasic, isRepeat:Boolean){

        mNoise?.start()
        if(isRepeat)    mTrial.repetitions++

        // to align bimodal stimuli, I have to delay the fastest modality by time_shift ms.
        // Thus I anticipate all main onsets by the same ms.
        // Since this code act for every kind of stimulus combination, I assume a trimodal stim
        val time_shift = when(trial.type){
            TEST_BISECTION_AUDIO_TACTILE    -> delaysAligner.getShift(STIM_AT, 0,0,-1)
            TEST_BISECTION_AUDIO_VIDEO      -> delaysAligner.getShift(STIM_AV, 0,-1,0)
            else                            -> 0
        }

        mStimuliHandler.postDelayed({
            deliverStimulus(trial as TrialBIS, TRIAL_STAGE_1)
            testEvent.accept(Pair(EVENT_STIMULI_START, null))
        }, FIRST_STIMULUS_DELAY - time_shift)

        mStimuliHandler.postDelayed({
            deliverStimulus(trial as TrialBIS, TRIAL_STAGE_2)
        }, (FIRST_STIMULUS_DELAY - time_shift + (trial as TrialBIS).stim_value))

        mStimuliHandler.postDelayed({
            deliverStimulus(trial, TRIAL_STAGE_3)
        }, (FIRST_STIMULUS_DELAY - time_shift + LAST_STIMULUS_DELAY))

        mStimuliHandler.postDelayed({
            onTrialEnd()
        }, (FIRST_STIMULUS_DELAY - time_shift + QUESTION_DELAY))
    }

    private fun deliverStimulus(trial: TrialBIS, stage:Int=0){

        when(trial.type) {
            TEST_BISECTION_AUDIO            ->  mStimuliManager.deliverAStimulus()
            TEST_BISECTION_TACTILE          ->  mStimuliManager.deliverTStimulus()
            TEST_BISECTION_AUDIO_TACTILE    ->  mStimuliManager.deliverAlignedStimulus(STIM_AT)
            TEST_BISECTION_AUDIO_VIDEO      ->  deliverAVStimuli(trial, stage)
        }
    }

    private fun deliverAVStimuli(trial:TrialBIS, stage:Int=0){

        mStimuliManager.mVisualManager!!.drawResOn = mDrawablesResource[stage]
        if(stage == TRIAL_STAGE_2){
            // mid (second) stimulus: audio and video are dissociated
            if(trial.conflict_type == STIMULUS_TYPE_VIDEO_AUDIO_LOG){
                val corr_delays = delaysAligner.arrangeDelays(STIM_AV, AV_STIMULUS_DELTA.toLong(),-1,0)
                mStimuliManager.deliverShiftedStimulus(STIM_AV, corr_delays.a, -1, corr_delays.v)
            }
            else{
                val corr_delays = delaysAligner.arrangeDelays(STIM_AV,0, -1, AV_STIMULUS_DELTA.toLong())
                mStimuliManager.deliverShiftedStimulus(STIM_AV, corr_delays.a, -1, corr_delays.v)
            }
        }
        // normal stimulus (1st or 3rd): audio and video simultaneously
        else    mStimuliManager.deliverAlignedStimulus(STIM_AV)
    }

    // =====================================================================================
    // DEBUG
    // =====================================================================================
    // Trial(val type:Int, val label:String, val conflict_type:String, val stim_value:Int, val duration:Int)
    // just one trial for each latency
    private var trialsDefaultSchema_debug: List<StimuliSetBIS> = listOf(StimuliSetBIS(2, 200, CONFLICT_TYPE_NONE))

    private fun createDefaultTrials_debug(stim_type_label:String, duration:Long, duration2:Long=0L):List<TrialBasic>{
        val trials:MutableList<TrialBasic> = mutableListOf()
        for(section in trialsDefaultSchema)
            for(i in 0 until 1){
                val corr_answ = if(section.stim_value < LAST_STIMULUS_DELAY/2)    0
                                else                                            1
                trials.add(TrialBIS(-1, subject.type, stim_type_label, corr_answ, section.stim_value, section.conflict, duration, duration2))
            }
        trials.shuffle()
        return trials
    }
    // =============================================================================================================================
}