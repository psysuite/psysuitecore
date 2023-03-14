package iit.uvip.psysuite.core.ui.subjects_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import iit.uvip.psysuite.core.R
import iit.uvip.psysuite.core.databinding.FragmentSubjectInfoBasicSpinnerBinding
import iit.uvip.psysuite.core.model.parcel.SubjectBasicParcel
import iit.uvip.psysuite.core.model.parcel.SubjectLongitParcel


open class SubjectLongitudinalDialogFragment : SubjectBasicSpinnerDialogFragment()
{
    override val LOG_TAG: String = SubjectLongitudinalDialogFragment::class.java.simpleName
//    private lateinit var binding: FragmentSubjectInfoBasicSpinnerBinding

    private var nSessions: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentSubjectInfoBasicSpinnerBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun initData(subj: SubjectBasicParcel) {

        super.initData(subj)
        ArrayAdapter.createFromResource(requireContext(), (subject as SubjectLongitParcel).test_sessions_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                (binding as FragmentSubjectInfoBasicSpinnerBinding).spinner.adapter = adapter
                nSessions = adapter.count
            }
        (binding as FragmentSubjectInfoBasicSpinnerBinding).spinner.setSelection((subj as SubjectLongitParcel).session)

        (binding as FragmentSubjectInfoBasicSpinnerBinding).labSpinner.text = resources.getString(R.string.session)
    }

    override fun checkData():List<String>{

        val errors = super.checkData() as MutableList<String>
        if ((binding as FragmentSubjectInfoBasicSpinnerBinding).spinner.selectedItemPosition == -1) errors.add(" - " + resources.getString(R.string.select_session))
        return errors
    }

    override fun updateSubject(): SubjectLongitParcel {

        subject = super.updateSubject() as SubjectLongitParcel

        (subject as SubjectLongitParcel).session = (binding as FragmentSubjectInfoBasicSpinnerBinding).spinner.selectedItemPosition
        return subject as SubjectLongitParcel
    }
}