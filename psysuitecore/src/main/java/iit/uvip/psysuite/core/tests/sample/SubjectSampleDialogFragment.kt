package iit.uvip.psysuite.core.tests.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import iit.uvip.psysuite.core.R
import iit.uvip.psysuite.core.databinding.FragmentSubjectInfoSampleBinding
import iit.uvip.psysuite.core.model.parcel.SubjectBasicParcel
import iit.uvip.psysuite.core.stimuli.StimuliManager
import iit.uvip.psysuite.core.tests.TestBasic
import iit.uvip.psysuite.core.ui.subjects_dialog.SubjectBasicDialogFragment
import org.albaspazio.core.ui.show2ChoisesDialog
import org.albaspazio.core.ui.showAlert


open class SubjectSampleDialogFragment: SubjectBasicDialogFragment(), AdapterView.OnItemSelectedListener
{
    override val LOG_TAG: String = SubjectSampleDialogFragment::class.java.simpleName
//    private lateinit var (binding as FragmentSubjectInfoSampleBinding):FragmentSubjectInfoSampleBinding

    companion object {
        @JvmStatic val EVENT_SUBJECT:String = "subject"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSubjectInfoSampleBinding.inflate(LayoutInflater.from(context))
        return (binding as FragmentSubjectInfoSampleBinding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // since this dialog does not have the txtName, I rewrite here all defined in SubjectBasicDialogFragment but for the code related to txtName
        super.onViewCreated(view, savedInstanceState)

        (binding as FragmentSubjectInfoSampleBinding).spCondition.onItemSelectedListener  = this
        (binding as FragmentSubjectInfoSampleBinding).spTactile.onItemSelectedListener    = this
        (binding as FragmentSubjectInfoSampleBinding).spAudio.onItemSelectedListener      = this
        (binding as FragmentSubjectInfoSampleBinding).spVisual.onItemSelectedListener     = this
    }

    override fun onResume() {

        val params                  = dialog?.window!!.attributes               // Get existing layout params for the window
        params.width                = WindowManager.LayoutParams.MATCH_PARENT   // Assign window properties to fill the parent
        params.height               = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window!!.attributes = params as WindowManager.LayoutParams

        super.onResume()
        setListeners()
    }

    // cannot call super.initData as some UI elements are missing
    override fun initData(subj: SubjectBasicParcel) {

        //------------------------------------------------------
        // SUB TASKS
        //------------------------------------------------------
        setConditions(mTaskCodeLabels)

        (binding as FragmentSubjectInfoSampleBinding).etDurationAudio.isEnabled   = false
        (binding as FragmentSubjectInfoSampleBinding).spAudio.isEnabled           = false
        (binding as FragmentSubjectInfoSampleBinding).spAudioResource.isEnabled   = false

        (binding as FragmentSubjectInfoSampleBinding).etDurationVisual.isEnabled  = false
        (binding as FragmentSubjectInfoSampleBinding).spVisual.isEnabled          = false

        (binding as FragmentSubjectInfoSampleBinding).spTactile.isEnabled         = false


        ArrayAdapter.createFromResource(requireContext(), R.array.sample_audio_types, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            (binding as FragmentSubjectInfoSampleBinding).spAudio.adapter = adapter
        }
        (binding as FragmentSubjectInfoSampleBinding).spAudio.setSelection(0)

        ArrayAdapter.createFromResource(requireContext(), R.array.sample_visual_types, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            (binding as FragmentSubjectInfoSampleBinding).spVisual.adapter = adapter
        }
        (binding as FragmentSubjectInfoSampleBinding).spVisual.setSelection(0)

        ArrayAdapter.createFromResource(requireContext(), R.array.sample_tactile_types, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            (binding as FragmentSubjectInfoSampleBinding).spTactile.adapter = adapter
        }
        (binding as FragmentSubjectInfoSampleBinding).spTactile.setSelection(0)

        ArrayAdapter.createFromResource(requireContext(), R.array.sample_audioassets_array, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            (binding as FragmentSubjectInfoSampleBinding).spAudioResource.adapter = adapter
        }
        (binding as FragmentSubjectInfoSampleBinding).spAudioResource.setSelection(0)

        (binding as FragmentSubjectInfoSampleBinding).etPairStimDistance.isEnabled = false

        (binding as FragmentSubjectInfoSampleBinding).etRepetitionNum.setText("10000")

        //------------------------------------------------------
        // NEXT TRIAL MODALITY
        //------------------------------------------------------
        // swInteractive is always visible
        subject.nextTrailModality = mNextTrialModes[0][0]   // button

        when (subject.nextTrailModality) {

            TestBasic.TEST_NEXTTRIAL_BUTTON -> {
                (binding as FragmentSubjectInfoSampleBinding).swInteractive?.isChecked = true
            }
            TestBasic.TEST_NEXTTRIAL_AUTO -> {
                (binding as FragmentSubjectInfoSampleBinding).swInteractive?.isChecked = false
            }
        }
        //------------------------------------------------------
        // noise
        (binding as FragmentSubjectInfoSampleBinding).swWhiteNoise.visibility     = View.VISIBLE
        (binding as FragmentSubjectInfoSampleBinding).swWhiteNoise.isChecked      = false
    }

    private fun setListeners() {
        (binding as FragmentSubjectInfoSampleBinding).btConfirm.setOnClickListener { confirmData() }
        (binding as FragmentSubjectInfoSampleBinding).btClear.setOnClickListener { clear() }
        (binding as FragmentSubjectInfoSampleBinding).btCancel.setOnClickListener { sendResult(null) }

        (binding as FragmentSubjectInfoSampleBinding).swAudio.setOnCheckedChangeListener { _, b ->
            (binding as FragmentSubjectInfoSampleBinding).etDurationAudio.isEnabled   = b
            (binding as FragmentSubjectInfoSampleBinding).spAudio.isEnabled           = b
            (binding as FragmentSubjectInfoSampleBinding).spAudioResource.isEnabled   = b

            if (b) updateAudio()
        }

        (binding as FragmentSubjectInfoSampleBinding).swVisual.setOnCheckedChangeListener { _, b ->
            (binding as FragmentSubjectInfoSampleBinding).etDurationVisual.isEnabled  = b
            (binding as FragmentSubjectInfoSampleBinding).spVisual.isEnabled          = b
        }

        (binding as FragmentSubjectInfoSampleBinding).swTactile.setOnCheckedChangeListener { _, b ->
            (binding as FragmentSubjectInfoSampleBinding).spTactile.isEnabled         = b
            (binding as FragmentSubjectInfoSampleBinding).etTactileAmplitudes.isEnabled   = b
            (binding as FragmentSubjectInfoSampleBinding).etTactileTimings.isEnabled= b

            if (b) updateTactile()
        }

        (binding as FragmentSubjectInfoSampleBinding).swInteractive?.setOnCheckedChangeListener { _, b ->
            subject.nextTrailModality = when (b) {
                true -> TestBasic.TEST_NEXTTRIAL_BUTTON
                false -> TestBasic.TEST_NEXTTRIAL_AUTO
            }
        }
    }
    //==========================================================================================================
    //  UPDATE UI ELEMENTS
    //==========================================================================================================
    override fun confirmData(){

        val errors = checkData()
        if(errors.isNotEmpty()){
            val str_errors = errors.joinToString("\n")
            showAlert(
                requireActivity(),
                resources.getString(R.string.warning),
                resources.getString(R.string.subject_info_notcorrected, str_errors)
            )
        }
        else {
            // data are valid => create subject object
            val subj = updateSubject()

            // in case the subject's "label_type_Date" file exists, ask user whether continue or change name
            if(manageSubjectFileExistence(subj)){
                // file is unique
                subject = subj as SubjectSampleParcel
                sendResult(subject)
            }
        }
    }

    // cannot call super.onClear as some UI elements are missing
    override fun clear(){

        (binding as FragmentSubjectInfoSampleBinding).swAudio.isChecked   = false
        (binding as FragmentSubjectInfoSampleBinding).swTactile.isChecked = false
        (binding as FragmentSubjectInfoSampleBinding).swVisual.isChecked  = false

        updateShifted(false)

        if (nConditions > 1)
            (binding as FragmentSubjectInfoSampleBinding).spCondition.setSelection(-1)

        if (subject.nextTrailModality == TestBasic.TEST_NEXTTRIAL_AUTO || subject.nextTrailModality == TestBasic.TEST_NEXTTRIAL_BUTTON) {
            (binding as FragmentSubjectInfoSampleBinding).swInteractive?.isChecked    = false
            subject.nextTrailModality   = TestBasic.TEST_NEXTTRIAL_AUTO
        }
        (binding as FragmentSubjectInfoSampleBinding).swWhiteNoise.isChecked = false
    }


    // on change spTactile/spAudio
    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        updateTactile()
        updateAudio()
        updateVisual()
        updateCondition()
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun updateCondition(){
        when((binding as FragmentSubjectInfoSampleBinding).spCondition.selectedItemPosition) {
            0   ->  {
                (binding as FragmentSubjectInfoSampleBinding).etPairStimDistance.isEnabled = false
                updateShifted(false)
            }
            1   ->  {
                (binding as FragmentSubjectInfoSampleBinding).etPairStimDistance.isEnabled = false
                updateShifted(true)
            }
            2   ->  {
                (binding as FragmentSubjectInfoSampleBinding).etPairStimDistance.isEnabled = true
                updateShifted(false)
            }
        }
    }

    private fun updateTactile(){
        when((binding as FragmentSubjectInfoSampleBinding).spTactile.selectedItemPosition) {
            0   ->  (binding as FragmentSubjectInfoSampleBinding).labTactileDuration.text = resources.getString(R.string.duration)
            1   ->  (binding as FragmentSubjectInfoSampleBinding).labTactileDuration.text = resources.getString(R.string.pattern)
        }
    }

    private fun updateVisual(){
        when((binding as FragmentSubjectInfoSampleBinding).spVisual.selectedItemPosition) {
            0   ->  {
                (binding as FragmentSubjectInfoSampleBinding).etVisualDrawableOff.isEnabled   = false
                (binding as FragmentSubjectInfoSampleBinding).etVisualDrawableOn.isEnabled    = false
            }
            1   ->  {
                (binding as FragmentSubjectInfoSampleBinding).etVisualDrawableOff.isEnabled   = true
                (binding as FragmentSubjectInfoSampleBinding).etVisualDrawableOn.isEnabled    = true
            }
        }
    }

    private fun updateAudio(){
        when((binding as FragmentSubjectInfoSampleBinding).spAudio.selectedItemPosition) {
            0   ->  (binding as FragmentSubjectInfoSampleBinding).spAudioResource.isEnabled   = false
            else   ->  (binding as FragmentSubjectInfoSampleBinding).spAudioResource.isEnabled   = true
        }
    }

    private fun updateShifted(enable:Boolean){
        (binding as FragmentSubjectInfoSampleBinding).etShiftedAudio.isEnabled    = enable
        (binding as FragmentSubjectInfoSampleBinding).etShiftedVisual.isEnabled   = enable
        (binding as FragmentSubjectInfoSampleBinding).etShiftedTactile.isEnabled  = enable
    }

    //------------------------------------------------------------------------------------
    // ACCESSORY
    //------------------------------------------------------------------------------------

    private fun calculateSources():Int{
        var src = 0
        if((binding as FragmentSubjectInfoSampleBinding).swAudio.isChecked) {
            src = when ((binding as FragmentSubjectInfoSampleBinding).spAudio.selectedItemPosition) {
                0       ->  src or StimuliManager.STIM_TYPE_A1
                1       ->  src or StimuliManager.STIM_TYPE_A2
                2       ->  src or StimuliManager.STIM_TYPE_A3
                else    ->  src or StimuliManager.STIM_TYPE_A4
            }
            (subject as SubjectSampleParcel).audioDuration   = (binding as FragmentSubjectInfoSampleBinding).etDurationAudio.text.toString().toLong()
            (subject as SubjectSampleParcel).audioResource   = (binding as FragmentSubjectInfoSampleBinding).spAudioResource.selectedItem as String
            (subject as SubjectSampleParcel).audioVolume     = (binding as FragmentSubjectInfoSampleBinding).etAudioVolume.text.toString().toInt()
        }

        if((binding as FragmentSubjectInfoSampleBinding).swTactile.isChecked) {
            src = when ((binding as FragmentSubjectInfoSampleBinding).spTactile.selectedItemPosition) {
                0       ->  src or StimuliManager.STIM_TYPE_T1
                else    ->  src or StimuliManager.STIM_TYPE_T2
            }
            (subject as SubjectSampleParcel).tactileAmplitudes  = (binding as FragmentSubjectInfoSampleBinding).etTactileAmplitudes.text.toString()
            (subject as SubjectSampleParcel).tactileTimings     = (binding as FragmentSubjectInfoSampleBinding).etTactileTimings.text.toString()
        }

        if((binding as FragmentSubjectInfoSampleBinding).swVisual.isChecked) {
            src = when ((binding as FragmentSubjectInfoSampleBinding).spVisual.selectedItemPosition) {
                0       ->  src or StimuliManager.STIM_TYPE_V1
                else    ->  src or StimuliManager.STIM_TYPE_V2
            }
            (subject as SubjectSampleParcel).visualDuration      = (binding as FragmentSubjectInfoSampleBinding).etDurationVisual.text.toString().toLong()
            (subject as SubjectSampleParcel).visualDrawableOn    = (binding as FragmentSubjectInfoSampleBinding).etVisualDrawableOn.text.toString().toInt()
            (subject as SubjectSampleParcel).visualDrawableOff   = (binding as FragmentSubjectInfoSampleBinding).etVisualDrawableOff.text.toString().toInt()
        }
        return src
    }

    // validate subject info
    override fun checkData():List<String>{

        val errors = mutableListOf<String>()

        if(calculateSources() == 0)                 errors.add(resources.getString(R.string.select_source))
        if ((binding as FragmentSubjectInfoSampleBinding).spCondition.selectedItemPosition == -1) errors.add(" - " + resources.getString(R.string.select_condition))

        return errors
    }

    // subject has been already validated
     override fun updateSubject(): SubjectBasicParcel{

        subject.type                = mTaskCodeLabels[(binding as FragmentSubjectInfoSampleBinding).spCondition.selectedItemPosition].id

        subject.nextTrailModality = when ((binding as FragmentSubjectInfoSampleBinding).swInteractive?.isChecked) {
            true -> TestBasic.TEST_NEXTTRIAL_BUTTON
            false -> TestBasic.TEST_NEXTTRIAL_AUTO
            null -> subject.nextTrailModality
        }

        (subject as SubjectSampleParcel).stim_sources = calculateSources()

        when((binding as FragmentSubjectInfoSampleBinding).spCondition.selectedItemPosition){
            1 -> (subject as SubjectSampleParcel).shiftedParams = listOf(   (binding as FragmentSubjectInfoSampleBinding).etShiftedAudio.text.toString().toLong(),
                (binding as FragmentSubjectInfoSampleBinding).etShiftedVisual.text.toString().toLong(),
                (binding as FragmentSubjectInfoSampleBinding).etShiftedTactile.text.toString().toLong())

            2 -> (subject as SubjectSampleParcel).pairDistance = if((binding as FragmentSubjectInfoSampleBinding).etPairStimDistance.text.toString().isEmpty()) 0L
                                                                 else    (binding as FragmentSubjectInfoSampleBinding).etPairStimDistance.text.toString().toLong()
        }

        (subject as SubjectSampleParcel).repetitions = (binding as FragmentSubjectInfoSampleBinding).etRepetitionNum.text.toString().toInt()

        if((subject as SubjectSampleParcel).repetitions > 1)
            (subject as SubjectSampleParcel).iti = (binding as FragmentSubjectInfoSampleBinding).etITI.text.toString().toLong()

        subject.whitenoise =    if((binding as FragmentSubjectInfoSampleBinding).swWhiteNoise.isChecked)  TestBasic.TEST_WNOISE_CHOOSE_ON
        else                        TestBasic.TEST_WNOISE_CHOOSE_OFF

        return subject
    }

    private fun sendResult(subj: SubjectBasicParcel?) {
        if (targetFragment == null)     return

        val intent = Intent()
        intent.putExtra(EVENT_SUBJECT, subj)
        targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }

    // check whether subject's "label_type_Date" file exists, ask user whether continue or change name
    private fun manageSubjectFileExistence(subj: SubjectBasicParcel):Boolean{
        return if(subj.existSubjectFile(requireContext()) > -1){
            show2ChoisesDialog(requireActivity(), resources.getString(R.string.warning),
                resources.getString(R.string.subject_present), resources.getString(R.string.yes), resources.getString(R.string.no),
                { // ok press, update subject, then continue
                    subject = subj as SubjectSampleParcel
                    sendResult(subject)
                },{})
            false
        }
        else true
    }
}