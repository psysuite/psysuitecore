package iit.uvip.psysuite.core.tests.tid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import iit.uvip.psysuite.core.R
import iit.uvip.psysuite.core.databinding.FragmentSubjectInfoTidBinding
import iit.uvip.psysuite.core.model.parcel.SubjectBasicParcel
import iit.uvip.psysuite.core.ui.subjects_dialog.SubjectLongitudinalDialogFragment
import iit.uvip.psysuite.core.utility.ConditionData

class SubjectTIDDialogFragment : SubjectLongitudinalDialogFragment(), AdapterView.OnItemSelectedListener
{
    override val LOG_TAG: String = SubjectTIDDialogFragment::class.java.simpleName
//    private lateinit var binding:FragmentSubjectInfoTidBinding

    private var selGroup: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSubjectInfoTidBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (binding as FragmentSubjectInfoTidBinding).spinner.onItemSelectedListener = this
        (binding as FragmentSubjectInfoTidBinding).spGroup.onItemSelectedListener = this
    }

    override fun initData(subj: SubjectBasicParcel) {
        super.initData(subj)

        //------------------------------------------------------
        // GROUPS <=> mTaskCodes
        //------------------------------------------------------
        val adapter:ArrayAdapter<ConditionData> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, mTaskCodeLabels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        (binding as FragmentSubjectInfoTidBinding).spGroup.adapter = adapter

        // nConditions coincided with number of available groups
        if (nConditions == 1) {
            // do not show condition spinner & set subject.type
            (binding as FragmentSubjectInfoTidBinding).labGroup.visibility = View.GONE
            (binding as FragmentSubjectInfoTidBinding).spGroup.visibility  = View.GONE
            (subject as SubjectTIDParcel).group = mTaskCodeLabels[0].id
            selGroup                            = 0
        }
        else if (nConditions > 1) {
            if((subject as SubjectTIDParcel).group != -1) {
                // set group spinner to subject.group
                mTaskCodeLabels.mapIndexed { index, taskCode ->
                    if (taskCode.id == (subject as SubjectTIDParcel).group){
                        (binding as FragmentSubjectInfoTidBinding).spGroup.setSelection(index, false)
                        selGroup            = index
                    }
                }
            }
            else {
                // set group spinner to first sub-task
                selGroup = 0
                (binding as FragmentSubjectInfoTidBinding).spGroup.setSelection(selGroup)
                (subject as SubjectTIDParcel).group = mTaskCodeLabels[0].id
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

        // spGroup and spCondition data coincides.
        // when selecting training sessions => selCondition = selGroup (and condition spinner gets disabled)

        // check session change
        when((binding as FragmentSubjectInfoTidBinding).spinner.selectedItemPosition){
            in 2..6   -> {
                        setConditions(listOf(mTaskCodeLabels[(binding as FragmentSubjectInfoTidBinding).spGroup.selectedItemPosition])) // make condition spinner GONE
                (binding as FragmentSubjectInfoTidBinding).spCondition.isEnabled = false
            }
            else      -> {

                        val selcond = mTaskCodeLabels[(binding as FragmentSubjectInfoTidBinding).spGroup.selectedItemPosition].id   // get current selected task
                        setConditions(mTaskCodeLabels)                                       // enable all

                        mTaskCodeLabels.mapIndexed { index, taskCode ->
                            if(taskCode.id == selcond)    (binding as FragmentSubjectInfoTidBinding).spCondition.setSelection(index)   // set what was selected before this change
                        }
                (binding as FragmentSubjectInfoTidBinding).spCondition.isEnabled = true
            }
        }
        (binding as FragmentSubjectInfoTidBinding).labCondition.visibility = View.VISIBLE
        (binding as FragmentSubjectInfoTidBinding).spCondition.visibility  = View.VISIBLE
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {}


    override fun checkData():List<String>{
        val errors = super.checkData() as MutableList<String>

        if((binding as FragmentSubjectInfoTidBinding).spinner.selectedItemPosition == 0)   errors.add(resources.getString(R.string.select_session))
        return errors
    }

    override fun clear() {
        super.clear()
        (binding as FragmentSubjectInfoTidBinding).spinner.setSelection(0)
    }

    override fun updateSubject(): SubjectTIDParcel{

        subject  = super.updateSubject() as SubjectTIDParcel

        (subject as SubjectTIDParcel).group     = mTaskCodeLabels[(binding as FragmentSubjectInfoTidBinding).spGroup.selectedItemPosition].id
        (subject as SubjectTIDParcel).session   = (binding as FragmentSubjectInfoTidBinding).spinner.selectedItemPosition - 1

        subject.type = if((binding as FragmentSubjectInfoTidBinding).spinner.selectedItemPosition in 2..6){
                                subject.showResult = true
                                mTaskCodeLabels[(binding as FragmentSubjectInfoTidBinding).spGroup.selectedItemPosition].id
                       }
                       else{
                                subject.showResult = false
                                mTaskCodeLabels[(binding as FragmentSubjectInfoTidBinding).spCondition.selectedItemPosition].id
                        }

        return subject as SubjectTIDParcel
    }
}