package iit.uvip.psysuite.core.tests.tid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import iit.uvip.psysuite.core.R
import iit.uvip.psysuite.core.common.TaskCode
import iit.uvip.psysuite.core.common.subjects_dialog.SubjectLongitudinalDialogFragment
import iit.uvip.psysuite.core.common.subjects_parcel.SubjectBasicParcel
import kotlinx.android.synthetic.main.fragment_subject_info_tid.*

class SubjectTIDDialogFragment : SubjectLongitudinalDialogFragment(), AdapterView.OnItemSelectedListener
{
    override val LOG_TAG: String = SubjectTIDDialogFragment::class.java.simpleName

    private var selGroup: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_subject_info_tid, container)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner.onItemSelectedListener = this
        spGroup.onItemSelectedListener = this
    }

    override fun initData(subj: SubjectBasicParcel) {
        super.initData(subj)

        //------------------------------------------------------
        // GROUPS <=> mTaskCodes
        //------------------------------------------------------
        val adapter:ArrayAdapter<TaskCode> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, mTaskCodes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGroup.adapter = adapter

        // nConditions coincided with number of available groups
        if (nConditions == 1) {
            // do not show condition spinner & set subject.type
            labGroup.visibility = View.GONE
            spGroup.visibility  = View.GONE
            (subject as SubjectTIDParcel).group = mTaskCodes[0].id
            selGroup                            = 0
        }
        else if (nConditions > 1) {
            if((subject as SubjectTIDParcel).group != -1) {
                // set group spinner to subject.group
                mTaskCodes.mapIndexed { index, taskCode ->
                    if (taskCode.id == (subject as SubjectTIDParcel).group){
                        spGroup.setSelection(index)
                        selGroup            = index
                    }
                }
            }
            else {
                // set group spinner to first sub-task
                selGroup = 0
                spGroup.setSelection(selGroup)
                (subject as SubjectTIDParcel).group = mTaskCodes[0].id
            }
        }
    }


    override fun onNothingSelected(parent: AdapterView<*>) {}

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

        // spGroup and spCondition data coincides.
        // when selecting training sessions => selCondition = selGroup (and condition spinner gets disabled)

        // session changed
        when(spinner.selectedItemPosition){
            in 2..6   -> {
                        setConditions(listOf(mTaskCodes[spGroup.selectedItemPosition])) // make condition spinner GONE
                        spCondition.isEnabled = false
            }
            else      -> {

                        val selcond = mTaskCodes[spGroup.selectedItemPosition].id   // get current selected task
                        setConditions(mTaskCodes)                                       // enable all

                        mTaskCodes.mapIndexed { index, taskCode ->
                            if(taskCode.id == selcond)    spCondition.setSelection(index)   // set what was selected before this change
                        }
                        spCondition.isEnabled = true
            }
        }
        labCondition.visibility = View.VISIBLE
        spCondition.visibility  = View.VISIBLE
    }

    override fun checkData():List<String>{
        val errors = super.checkData() as MutableList<String>

        if(spinner.selectedItemPosition == 0)   errors.add(resources.getString(R.string.select_session))
        return errors
    }

    override fun clear() {
        super.clear()
        spinner.setSelection(0)
    }

    override fun updateSubject(): SubjectTIDParcel{

        subject  = super.updateSubject() as SubjectTIDParcel

        (subject as SubjectTIDParcel).group     = mTaskCodes[spGroup.selectedItemPosition].id
        (subject as SubjectTIDParcel).session   = spinner.selectedItemPosition - 1

        subject.type = if(spinner.selectedItemPosition in 2..6) mTaskCodes[spGroup.selectedItemPosition].id
                       else                                     mTaskCodes[spCondition.selectedItemPosition].id

        return subject as SubjectTIDParcel
    }
}