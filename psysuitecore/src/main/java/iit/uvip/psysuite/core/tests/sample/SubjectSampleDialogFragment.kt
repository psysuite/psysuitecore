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
    private lateinit var binding:FragmentSubjectInfoSampleBinding

    companion object {
        @JvmStatic val EVENT_SUBJECT:String = "subject"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSubjectInfoSampleBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // since this dialog does not have the txtName, I rewrite here all defined in SubjectBasicDialogFragment but for the code related to txtName
        super.onViewCreated(view, savedInstanceState)

        binding.spCondition.onItemSelectedListener  = this
        binding.spTactile.onItemSelectedListener    = this
        binding.spAudio.onItemSelectedListener      = this
        binding.spVisual.onItemSelectedListener     = this
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

        binding.etDurationAudio.isEnabled   = false
        binding.spAudio.isEnabled           = false
        binding.spAudioResource.isEnabled   = false

        binding.etDurationVisual.isEnabled  = false
        binding.spVisual.isEnabled          = false

        binding.spTactile.isEnabled         = false


        ArrayAdapter.createFromResource(requireContext(), R.array.sample_audio_types, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spAudio.adapter = adapter
        }
        binding.spAudio.setSelection(0)

        ArrayAdapter.createFromResource(requireContext(), R.array.sample_visual_types, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spVisual.adapter = adapter
        }
        binding.spVisual.setSelection(0)

        ArrayAdapter.createFromResource(requireContext(), R.array.sample_tactile_types, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spTactile.adapter = adapter
        }
        binding.spTactile.setSelection(0)

        ArrayAdapter.createFromResource(requireContext(), R.array.sample_audioassets_array, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spAudioResource.adapter = adapter
        }
        binding.spAudioResource.setSelection(0)

        binding.etPairStimDistance.isEnabled = false

        binding.etRepetitionNum.setText("10000")

        //------------------------------------------------------
        // NEXT TRIAL MODALITY
        //------------------------------------------------------
        // swInteractive is always visible
        subject.nextTrailModality = mNextTrialModes[0][0]   // button

        when (subject.nextTrailModality) {

            TestBasic.TEST_NEXTTRIAL_BUTTON -> {
                binding.swInteractive?.isChecked = true
            }
            TestBasic.TEST_NEXTTRIAL_AUTO -> {
                binding.swInteractive?.isChecked = false
            }
        }
        //------------------------------------------------------
        // noise
        binding.swWhiteNoise.visibility     = View.VISIBLE
        binding.swWhiteNoise.isChecked      = false
    }

    private fun setListeners() {
        binding.btConfirm.setOnClickListener { confirmData() }
        binding.btClear.setOnClickListener { clear() }
        binding.btCancel.setOnClickListener { sendResult(null) }

        binding.swAudio.setOnCheckedChangeListener { _, b ->
            binding.etDurationAudio.isEnabled   = b
            binding.spAudio.isEnabled           = b
            binding.spAudioResource.isEnabled   = b

            if (b) updateAudio()
        }

        binding.swVisual.setOnCheckedChangeListener { _, b ->
            binding.etDurationVisual.isEnabled  = b
            binding.spVisual.isEnabled          = b
        }

        binding.swTactile.setOnCheckedChangeListener { _, b ->
            binding.spTactile.isEnabled         = b
            binding.etTactileAmplitudes.isEnabled   = b
            binding.etTactileTimings.isEnabled= b

            if (b) updateTactile()
        }

        binding.swInteractive?.setOnCheckedChangeListener { _, b ->
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

        binding.swAudio.isChecked   = false
        binding.swTactile.isChecked = false
        binding.swVisual.isChecked  = false

        updateShifted(false)

        if (nConditions > 1)
            binding.spCondition.setSelection(-1)

        if (subject.nextTrailModality == TestBasic.TEST_NEXTTRIAL_AUTO || subject.nextTrailModality == TestBasic.TEST_NEXTTRIAL_BUTTON) {
            binding.swInteractive?.isChecked    = false
            subject.nextTrailModality   = TestBasic.TEST_NEXTTRIAL_AUTO
        }
        binding.swWhiteNoise.isChecked = false
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
        when(binding.spCondition.selectedItemPosition) {
            0   ->  {
                binding.etPairStimDistance.isEnabled = false
                updateShifted(false)
            }
            1   ->  {
                binding.etPairStimDistance.isEnabled = false
                updateShifted(true)
            }
            2   ->  {
                binding.etPairStimDistance.isEnabled = true
                updateShifted(false)
            }
        }
    }

    private fun updateTactile(){
        when(binding.spTactile.selectedItemPosition) {
            0   ->  binding.labTactileDuration.text = resources.getString(R.string.duration)
            1   ->  binding.labTactileDuration.text = resources.getString(R.string.pattern)
        }
    }

    private fun updateVisual(){
        when(binding.spVisual.selectedItemPosition) {
            0   ->  {
                binding.etVisualDrawableOff.isEnabled   = false
                binding.etVisualDrawableOn.isEnabled    = false
            }
            1   ->  {
                binding.etVisualDrawableOff.isEnabled   = true
                binding.etVisualDrawableOn.isEnabled    = true
            }
        }
    }

    private fun updateAudio(){
        when(binding.spAudio.selectedItemPosition) {
            0   ->  binding.spAudioResource.isEnabled   = false
            else   ->  binding.spAudioResource.isEnabled   = true
        }
    }

    private fun updateShifted(enable:Boolean){
        binding.etShiftedAudio.isEnabled    = enable
        binding.etShiftedVisual.isEnabled   = enable
        binding.etShiftedTactile.isEnabled  = enable
    }

    //------------------------------------------------------------------------------------
    // ACCESSORY
    //------------------------------------------------------------------------------------

    private fun calculateSources():Int{
        var src = 0
        if(binding.swAudio.isChecked) {
            src = when (binding.spAudio.selectedItemPosition) {
                0       ->  src or StimuliManager.STIM_TYPE_A1
                1       ->  src or StimuliManager.STIM_TYPE_A2
                2       ->  src or StimuliManager.STIM_TYPE_A3
                else    ->  src or StimuliManager.STIM_TYPE_A4
            }
            (subject as SubjectSampleParcel).audioDuration   = binding.etDurationAudio.text.toString().toLong()
            (subject as SubjectSampleParcel).audioResource   = binding.spAudioResource.selectedItem as String
            (subject as SubjectSampleParcel).audioVolume     = binding.etAudioVolume.text.toString().toInt()
        }

        if(binding.swTactile.isChecked) {
            src = when (binding.spTactile.selectedItemPosition) {
                0       ->  src or StimuliManager.STIM_TYPE_T1
                else    ->  src or StimuliManager.STIM_TYPE_T2
            }
            (subject as SubjectSampleParcel).tactileAmplitudes  = binding.etTactileAmplitudes.text.toString()
            (subject as SubjectSampleParcel).tactileTimings     = binding.etTactileTimings.text.toString()
        }

        if(binding.swVisual.isChecked) {
            src = when (binding.spVisual.selectedItemPosition) {
                0       ->  src or StimuliManager.STIM_TYPE_V1
                else    ->  src or StimuliManager.STIM_TYPE_V2
            }
            (subject as SubjectSampleParcel).visualDuration      = binding.etDurationVisual.text.toString().toLong()
            (subject as SubjectSampleParcel).visualDrawableOn    = binding.etVisualDrawableOn.text.toString().toInt()
            (subject as SubjectSampleParcel).visualDrawableOff   = binding.etVisualDrawableOff.text.toString().toInt()
        }
        return src
    }

    // validate subject info
    override fun checkData():List<String>{

        val errors = mutableListOf<String>()

        if(calculateSources() == 0)                 errors.add(resources.getString(R.string.select_source))
        if (binding.spCondition.selectedItemPosition == -1) errors.add(" - " + resources.getString(R.string.select_condition))

        return errors
    }

    // subject has been already validated
     override fun updateSubject(): SubjectBasicParcel{

        subject.type                = mTaskCodeLabels[binding.spCondition.selectedItemPosition].id

        subject.nextTrailModality = when (binding.swInteractive?.isChecked) {
            true -> TestBasic.TEST_NEXTTRIAL_BUTTON
            false -> TestBasic.TEST_NEXTTRIAL_AUTO
            null -> subject.nextTrailModality
        }

        (subject as SubjectSampleParcel).stim_sources = calculateSources()

        when(binding.spCondition.selectedItemPosition){
            1 -> (subject as SubjectSampleParcel).shiftedParams = listOf(   binding.etShiftedAudio.text.toString().toLong(),
                binding.etShiftedVisual.text.toString().toLong(),
                binding.etShiftedTactile.text.toString().toLong())

            2 -> (subject as SubjectSampleParcel).pairDistance = if(binding.etPairStimDistance.text.toString().isEmpty()) 0L
                                                                 else    binding.etPairStimDistance.text.toString().toLong()
        }

        (subject as SubjectSampleParcel).repetitions = binding.etRepetitionNum.text.toString().toInt()

        if((subject as SubjectSampleParcel).repetitions > 1)
            (subject as SubjectSampleParcel).iti = binding.etITI.text.toString().toLong()

        subject.whitenoise =    if(binding.swWhiteNoise.isChecked)  TestBasic.TEST_WNOISE_CHOOSE_ON
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