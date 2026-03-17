package org.albaspazio.psysuite.tests.tir

import android.app.Activity
import android.content.Context
import android.os.SystemClock.uptimeMillis
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.VISIBLE
import android.view.View.generateViewId
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import org.albaspazio.psysuite.core.R
import org.albaspazio.psysuite.core.databinding.FragmentTestBinding
import org.albaspazio.psysuite.model.Populations
import org.albaspazio.psysuite.model.SubjectBasicParcel
import org.albaspazio.psysuite.stimuli.AudioManager
import org.albaspazio.psysuite.stimuli.ImageViewDefinedException
import org.albaspazio.psysuite.stimuli.StimuliManager
import org.albaspazio.psysuite.stimuli.TactileManager
import org.albaspazio.psysuite.stimuli.VibratorNotDefinedException
import org.albaspazio.psysuite.stimuli.VisualManager
import org.albaspazio.psysuite.tests.TestBasic
import org.albaspazio.psysuite.tests.tsp.TestTSP
import org.albaspazio.psysuite.trials.FixedTrialsManager
import org.albaspazio.psysuite.trials.TrialBasic
import org.albaspazio.psysuite.ui.fragments.TestFragment
import org.albaspazio.psysuite.utility.ConditionData
import org.albaspazio.psysuite.utility.StimuliSet
import org.albaspazio.core.accessory.VibrationManager
import org.albaspazio.core.speech.SpeechManager
import org.albaspazio.core.ui.showToast



class TestTIR(ctx: Context,
              activity: Activity,
              hostfragment: Fragment,
              subject: SubjectBasicParcel,
              vibrator: VibrationManager?,
              mImageView: ImageView?,
              speechManager: SpeechManager?,
              mainView: View
) : TestBasic(ctx, activity, hostfragment, subject, vibrator, mImageView, speechManager, mainView)  {

    override var LOG_TAG: String = TestTIR::class.java.simpleName

    companion object {
        @JvmStatic val TEST_BASIC_LABEL = "TIR"
        @JvmStatic val N_TRIALS = 50  //

        @JvmStatic val STIMULUS_DURATION_AUDIO      = 2000L // indicates the duration of the wav file to be loaded

        fun getConditionsInfo(ctx: Context): List<ConditionData> = mutableListOf(
            ConditionData("${STIMULUS_TYPE_VISUAL_LOG}_$STIMULUS_ISI_SUB",     TEST_TIR_V_SUB,        "${TEST_BASIC_LABEL}_${STIMULUS_TYPE_VISUAL_LOG}_${STIMULUS_ISI_SUB}" ,   Populations.sighted_populations),
            ConditionData("${STIMULUS_TYPE_AUDIO_LOG}_$STIMULUS_ISI_SUB",     TEST_TIR_A_SUB,        "${TEST_BASIC_LABEL}_${STIMULUS_TYPE_AUDIO_LOG}_${STIMULUS_ISI_SUB}" ,   Populations.hearing_populations),
            ConditionData("${STIMULUS_TYPE_TACTILE_LOG}_$STIMULUS_ISI_SUB",     TEST_TIR_T_SUB,        "${TEST_BASIC_LABEL}_${STIMULUS_TYPE_TACTILE_LOG}_${STIMULUS_ISI_SUB}" ,   Populations.all_populations),
            ConditionData("${STIMULUS_TYPE_VISUAL_LOG}_$STIMULUS_ISI_SUPRA",   TEST_TIR_V_SUPRA,        "${TEST_BASIC_LABEL}_${STIMULUS_TYPE_VISUAL_LOG}_${STIMULUS_ISI_SUPRA}" , Populations.sighted_populations),
            ConditionData("${STIMULUS_TYPE_AUDIO_LOG}_$STIMULUS_ISI_SUPRA",   TEST_TIR_A_SUPRA,        "${TEST_BASIC_LABEL}_${STIMULUS_TYPE_AUDIO_LOG}_${STIMULUS_ISI_SUPRA}" , Populations.hearing_populations),
            ConditionData("${STIMULUS_TYPE_TACTILE_LOG}_$STIMULUS_ISI_SUPRA",   TEST_TIR_T_SUPRA,        "${TEST_BASIC_LABEL}_${STIMULUS_TYPE_TACTILE_LOG}_${STIMULUS_ISI_SUPRA}" , Populations.all_populations),
            ConditionData("${STIMULUS_TYPE_VISUAL_LOG}_${STIMULUS_ISI_SUBSUPRA}",   TEST_TIR_V_SUBSUPRA,        "${TEST_BASIC_LABEL}_${STIMULUS_TYPE_VISUAL_LOG}_${STIMULUS_ISI_SUBSUPRA}" , Populations.sighted_populations),
            ConditionData("${STIMULUS_TYPE_AUDIO_LOG}_${STIMULUS_ISI_SUBSUPRA}",   TEST_TIR_A_SUBSUPRA,        "${TEST_BASIC_LABEL}_${STIMULUS_TYPE_AUDIO_LOG}_${STIMULUS_ISI_SUBSUPRA}" , Populations.hearing_populations),
            ConditionData("${STIMULUS_TYPE_TACTILE_LOG}_${STIMULUS_ISI_SUBSUPRA}",   TEST_TIR_T_SUBSUPRA,        "${TEST_BASIC_LABEL}_${STIMULUS_TYPE_TACTILE_LOG}_${STIMULUS_ISI_SUBSUPRA}" , Populations.all_populations),
        )

        fun getNextTrialModes(ctx:Context):List<List<Int>> =  listOf(
            listOf(TEST_NEXTTRIAL_AUTO),listOf(TEST_NEXTTRIAL_AUTO),listOf(TEST_NEXTTRIAL_AUTO),
            listOf(TEST_NEXTTRIAL_AUTO),listOf(TEST_NEXTTRIAL_AUTO),listOf(TEST_NEXTTRIAL_AUTO),
            listOf(TEST_NEXTTRIAL_AUTO),listOf(TEST_NEXTTRIAL_AUTO),listOf(TEST_NEXTTRIAL_AUTO)
        )
    }
    // region: DEFINE TRIALS SCHEMA: stimulus type & delay

//    private var trialsSchemaSub: List<StimuliSetTIR> = createEquallySpacedInts(450, 850, N_TRIALS).map {
//        StimuliSetTIR(1, it.toFloat())}

//    private var trialsSchemaSupra: List<StimuliSetTIR> = createEquallySpacedInts(1500, 2000, N_TRIALS).map {
//        StimuliSetTIR(1, it.toFloat())}

    private var trialsSchemaSub:List<StimuliSet> = listOf(
        StimuliSet(2, 500F),
        StimuliSet(2, 540F),
        StimuliSet(2, 585F),
        StimuliSet(2, 630F),
        StimuliSet(2, 680F),
        StimuliSet(2, 735F),
        StimuliSet(2, 795F),
        StimuliSet(2, 860F),
    )
    private var trialsSchemaSupra:List<StimuliSet> = listOf(
        StimuliSet(2, 1000F),
        StimuliSet(2, 1080F),
        StimuliSet(2, 1170F),
        StimuliSet(2, 1265F),
        StimuliSet(2, 1370F),
        StimuliSet(2, 1480F),
        StimuliSet(2, 1600F),
        StimuliSet(2, 1730F),
    )
    private var trialsSchemaSubSupra:List<StimuliSet> = trialsSchemaSub + trialsSchemaSupra


    private val schema: List<StimuliSet> = when (subject.type) {

        TEST_TIR_V_SUB, TEST_TIR_A_SUB, TEST_TIR_T_SUB          -> trialsSchemaSub
        TEST_TIR_V_SUPRA, TEST_TIR_A_SUPRA, TEST_TIR_T_SUPRA    -> trialsSchemaSupra
        else                                                    -> trialsSchemaSubSupra
    }

    private val nBlocks = 1

    // endregion

    private val binding: FragmentTestBinding =  (hostfragment as TestFragment).binding

    override var mDrawablesResource: MutableList<Int> = mutableListOf(R.drawable.black_circle)
    private var currImageRes:Int        = 0

    private var trialAbortTime:Long = 0

    private var trialStartMs:Long               = 0L                    // trial onset
    private var trialEndMs:Long                 = trialAbortTime        // user press latency

    // region RESPONSE BUTTON
    private lateinit var mRespButton:Button
    private var parent_layout_width:Int         = 0
    private var parent_layout_height:Int        = 0
    private lateinit var mLayout:ConstraintLayout
    private val isLandscape: Boolean = false
    // endregion
    
    override fun initTest(){
        
        mLayout = binding.root

        when {
            mImageView == null && (subject.type == TEST_TIR_V_SUB || subject.type == TEST_TIR_V_SUPRA || subject.type == TEST_TIR_V_SUBSUPRA)  -> throw ImageViewDefinedException("IMAGE_VIEW_NOT_DEFINED")
            vibrator == null && (subject.type == TEST_TIR_T_SUB || subject.type == TEST_TIR_T_SUPRA || subject.type == TEST_TIR_T_SUBSUPRA)    -> throw VibratorNotDefinedException("VIBRATOR_NOT_DEFINED")
            mainView == null                                                                                                                   -> throw ImageViewDefinedException("MAIN_VIEW_NOT_DEFINED")
        }

        // set question & create mTrials list
        validAnswers    = mutableListOf()
        mQuestion       = ""

        when(subject.type){
            TEST_TIR_V_SUB, TEST_TIR_V_SUPRA, TEST_TIR_V_SUBSUPRA    -> {
                currStimulusDuration    = STIMULUS_DURATION_VISUAL
                currStimulusLabel       = "${TEST_BASIC_LABEL}_${STIMULUS_TYPE_VISUAL_LOG}"
            }
            TEST_TIR_A_SUB, TEST_TIR_A_SUPRA, TEST_TIR_A_SUBSUPRA    -> {
                currStimulusDuration    = STIMULUS_DURATION_AUDIO
                currStimulusLabel       = "${TEST_BASIC_LABEL}_${STIMULUS_TYPE_AUDIO_LOG}"
            }
            else                                -> {
                currStimulusDuration    = STIMULUS_DURATION_TACTILE
                currStimulusLabel       = "${TEST_BASIC_LABEL}_${STIMULUS_TYPE_TACTILE_LOG}"
            }
        }
        currImageRes            = mDrawablesResource[0]

        mTestLabel              = ""
        getConditionsInfo(ctx).map { if (it.id == subject.type) mTestLabel = it.label }
        if(mTestLabel.isEmpty()) showToast("Should not happen. given test code was not recognized", ctx)

        mTrialsManager =
            when (subject.isDebug) {
                true -> {
                    val trials = createTrialsDebug()
                    FixedTrialsManager(trials as MutableList<TrialBasic>)
                }
                else -> FixedTrialsManager(createFixedTrials() as MutableList<TrialBasic>)
            }
        createResultFile(TrialTIR.LOG_HEADER)

        mStimuliManager = when(subject.type){

            TEST_TIR_V_SUB, TEST_TIR_V_SUPRA, TEST_TIR_V_SUBSUPRA -> {
                StimuliManager(null, null,
                    VisualManager(STIM_T, mImageView!!, currImageRes, duration = currStimulusDuration, handler = mStimuliHandler),
                    subject.stimuliDelays, ctx, mStimuliHandler)
            }
            TEST_TIR_A_SUB, TEST_TIR_A_SUPRA, TEST_TIR_A_SUBSUPRA -> {
                StimuliManager(AudioManager(STIM_A, audioResources[STIMULUS_DURATION_AUDIO] ?: "t1000hz_50ms.wav",  duration = STIMULUS_DURATION_AUDIO, handler = mStimuliHandler, ctx = ctx), null,null,
                    subject.stimuliDelays, ctx, mStimuliHandler)
            }
            else -> StimuliManager(null, TactileManager(vibrator!!, duration = STIMULUS_DURATION_TACTILE, handler = mStimuliHandler), null,
                subject.stimuliDelays, ctx, mStimuliHandler)
        }

        mRespButton             = createResponseButton("press", mLayout, ::onPress, ::onRelease)
        mRespButton.visibility  = View.INVISIBLE

        testEvent.accept(Triple(EVENT_TEST_SETUP_COMPLETED, null, listOf()))
    }

    // ===================================================================================
    // region CREATE TRIALS
    // ===================================================================================
    private fun createFixedTrials():List<TrialBasic> {

        val trials:MutableList<TrialBasic> = mutableListOf()
        var temp_trials:MutableList<TrialBasic> = mutableListOf()

        for(i in 0 until nBlocks){
            temp_trials = mutableListOf()
            for(section in schema)
                for(i in 0 until section.ntrials)
                    temp_trials.add(TrialTIR(-1, subject.type, currStimulusLabel, section.magnitude))
            temp_trials.shuffle()
            trials.addAll(temp_trials)
        }
        return trials
    }

    private fun createTrialsDebug():List<TrialBasic> {
        val trials: MutableList<TrialBasic> = mutableListOf()

        return trials
    }

    // =============================================================================================================================
    // DELIVER STIMULI
    // =============================================================================================================================
    override fun show(trial: TrialBasic, isRepeat: Boolean) {

        trialAbortTime = 4*trial.stim_value    // allowed number of ms to wait for user response. after this interval the trial ends

        if(subject.whitenoise == TEST_SWITCH_ENABLED) mNoise?.start()

        if(isRepeat)    mTrial.repetitions++

        mStimuliHandler.postDelayed({
            deliverStimulus(trial as TrialTIR)
            testEvent.accept(Triple(EVENT_STIMULI_START, null, listOf()))
        }, FIRST_STIMULUS_DELAY)

        mStimuliHandler.postDelayed({
            mRespButton.visibility = VISIBLE
        }, FIRST_STIMULUS_DELAY + trial.stim_value + 100L)

        mStimuliHandler.postDelayed({
            onStimuliEnd()
        }, (TestTSP.Companion.FIRST_STIMULUS_DELAY + trialAbortTime))
    }

    private fun createResponseButton(txt:String, parent_layout:ConstraintLayout, onPress:() -> Unit, onRelease:() -> Unit): Button {

        parent_layout_width     = parent_layout.width
        parent_layout_height    = parent_layout.height

        mRespButton = AppCompatButton(ctx).apply {
            id              = generateViewId()
            text            = txt
            textAlignment   = TextView.TEXT_ALIGNMENT_CENTER
            gravity         = Gravity.CENTER
            visibility      = VISIBLE

            parent_layout.addView(this)

            if(isLandscape) {
                x = (parent_layout_width*0.8).toFloat()
                y = (parent_layout_height*0.1).toFloat()

                layoutParams.width = (parent_layout_width*0.15).toInt()
                layoutParams.height = (parent_layout_height*0.8).toInt()
            }
            else{
                x = (parent_layout_width*0.1).toFloat()
                y = (parent_layout_height*0.7).toFloat()

                layoutParams.width  = (parent_layout_width*0.8).toInt()
                layoutParams.height = (parent_layout_height*0.25).toInt()
            }

            setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
//            setTextAppearance(TextAppearance_AppCompat_Widget_Button_Colored)
            setLinkTextColor(context.resources.getColor(R.color.colorPrimary))
        }

        mRespButton.apply {
            setOnTouchListener { v, event ->
                val action = event.action
                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        onPress()
                        performClick()
                    }
                    MotionEvent.ACTION_UP ->    onRelease()
                }
                true
            }
        } as Button

        return mRespButton
    }

    private fun onPress(){
        trialStartMs = uptimeMillis()
    }

    private fun onRelease(){
        trialEndMs = uptimeMillis() - trialStartMs      // behavioral result
        onStimuliEnd()
    }

    private fun deliverStimulus(trial: TrialTIR){
        when(trial.type) {
            TEST_TIR_A_SUB, TEST_TIR_A_SUPRA, TEST_TIR_A_SUBSUPRA ->  mStimuliManager.deliverAStimulus(duration = trial.stim_value)
            TEST_TIR_V_SUB, TEST_TIR_V_SUPRA, TEST_TIR_V_SUBSUPRA ->  mStimuliManager.deliverVStimulus(duration = trial.stim_value)
            TEST_TIR_T_SUB, TEST_TIR_T_SUPRA, TEST_TIR_T_SUBSUPRA ->  mStimuliManager.deliverTStimulus(duration = trial.stim_value)
            else -> throw Exception("STIMULUS_TYPE_NOT_DEFINED")
        }
    }

    override fun onStimuliEnd() {
        mStimuliHandler.removeCallbacksAndMessages(null)
        mRespButton.visibility = View.INVISIBLE
        setAnswer(trialEndMs.toInt())

        super.onStimuliEnd()
    }

    override fun initSummary() {}
}