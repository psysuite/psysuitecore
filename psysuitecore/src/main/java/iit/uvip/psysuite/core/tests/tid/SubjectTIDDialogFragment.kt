package iit.uvip.psysuite.core.tests.tid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import iit.uvip.psysuite.core.R
import iit.uvip.psysuite.core.common.subjects_dialog.SubjectLongitudinalDialogFragment
import iit.uvip.psysuite.core.common.subjects_parcel.SubjectBasicParcel
import kotlinx.android.synthetic.main.fragment_subject_info_tid.*

class SubjectTIDDialogFragment : SubjectLongitudinalDialogFragment()
{
    override val LOG_TAG: String = SubjectTIDDialogFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_subject_info_tid, container)
    }

    override fun initData(subj: SubjectBasicParcel) {
        super.initData(subj)
        radioGroupFirstModality.check(radioGroupFirstModality.getChildAt((subj as SubjectTIDParcel).first_modality).id)
    }

    override fun clear() {
        super.clear()
        radioGroupFirstModality.clearCheck()
    }

    override fun checkData():List<String>{
        val errors = super.checkData() as MutableList<String>

        if(radioGroupFirstModality.checkedRadioButtonId == -1) errors.add(resources.getString(R.string.tid_select_training_first_modality))

        return errors
    }

    override fun updateSubject(): SubjectTIDParcel{

        subject                                         = super.updateSubject() as SubjectTIDParcel
        (subject as SubjectTIDParcel).first_modality    = radioGroupFirstModality.indexOfChild(radioGroupFirstModality.findViewById(radioGroupFirstModality.checkedRadioButtonId))

        return subject as SubjectTIDParcel
    }
}