package iit.uvip.psysuite.core.ui.subjects_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import iit.uvip.psysuite.core.R
import iit.uvip.psysuite.core.databinding.FragmentSubjectInfoBasicBinding
import iit.uvip.psysuite.core.databinding.FragmentSubjectInfoBasicSpinnerBinding
import iit.uvip.psysuite.core.model.parcel.SubjectBasicListParcel
import iit.uvip.psysuite.core.model.parcel.SubjectBasicParcel


open class SubjectBasicSpinnerDialogFragment : SubjectBasicDialogFragment()
{
    override val LOG_TAG:String                 = SubjectBasicSpinnerDialogFragment::class.java.simpleName
//    override lateinit var binding:FragmentSubjectInfoBasicSpinnerBinding

    private var nSpinnerElements: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentSubjectInfoBasicSpinnerBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)

//        binding = FragmentSubjectInfoBasicSpinnerBinding.inflate(LayoutInflater.from(context))
    }

    override fun initData(subj: SubjectBasicParcel) {

        super.initData(subj)

        ArrayAdapter.createFromResource(requireContext(), (subject as SubjectBasicListParcel).spinner_data_resource, android.R.layout.simple_spinner_item)
        .also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            (binding as FragmentSubjectInfoBasicSpinnerBinding).spinner.adapter = adapter
            nSpinnerElements = adapter.count
        }
        (binding as FragmentSubjectInfoBasicSpinnerBinding).spinner.setSelection((subj as SubjectBasicListParcel).spinner_sel, false)

        (binding as FragmentSubjectInfoBasicSpinnerBinding).labSpinner.text = (subject as SubjectBasicListParcel).spinner_label
    }

    override fun clear(){
        super.clear()
        (binding as FragmentSubjectInfoBasicSpinnerBinding).spinner.setSelection(-1)
    }

    override fun checkData():List<String>{

        val errors = super.checkData() as MutableList<String>
        if ((binding as FragmentSubjectInfoBasicSpinnerBinding).spinner.selectedItemPosition == -1) errors.add(" - " + resources.getString(R.string.select_spinner, (binding as FragmentSubjectInfoBasicSpinnerBinding).labSpinner.text) )
        return errors
    }

    override fun updateSubject(): SubjectBasicListParcel{

        subject = super.updateSubject() as SubjectBasicListParcel

        (subject as SubjectBasicListParcel).spinner_sel = (binding as FragmentSubjectInfoBasicSpinnerBinding).spinner.selectedItemPosition
        return subject as SubjectBasicListParcel
    }
}