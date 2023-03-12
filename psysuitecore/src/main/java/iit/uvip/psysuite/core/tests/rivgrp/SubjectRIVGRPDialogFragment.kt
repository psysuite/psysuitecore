package iit.uvip.psysuite.core.tests.rivgrp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import iit.uvip.psysuite.core.R
import iit.uvip.psysuite.core.databinding.FragmentSubjectInfoBasicRivgrpBinding
import iit.uvip.psysuite.core.model.parcel.SubjectBasicParcel
import iit.uvip.psysuite.core.ui.subjects_dialog.SubjectBasicDialogFragment


class SubjectRIVGRPDialogFragment : SubjectBasicDialogFragment(), AdapterView.OnItemSelectedListener
{
    override val LOG_TAG: String = SubjectRIVGRPDialogFragment::class.java.simpleName

    private lateinit var binding: FragmentSubjectInfoBasicRivgrpBinding

    private var isRivalryFirst: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSubjectInfoBasicRivgrpBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.spCondition.onItemSelectedListener = this

        binding.swFirstCond.setOnCheckedChangeListener { _, isChecked ->
            binding.swFirstCond.text =  if(isChecked)  "rivalry"
                                        else           "grouping"
        }
    }

    override fun initData(subj: SubjectBasicParcel) {
        super.initData(subj)

        binding.txtDurBlocks.setText(((subj as SubjectRIVGRPParcel).blockDuration/1000).toString())
        binding.txtNBlocks.setText(subj.totBlocks.toString())

        binding.swFirstCond.isChecked   = isRivalryFirst
        binding.swFirstCond.text        = "rivalry"
    }


    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

        // spGroup and spCondition data coincides.
        // when selecting training sessions => selCondition = selGroup (and condition spinner gets disabled)

        // check session change
        when(binding.spCondition.selectedItemPosition){
            2,5   -> {
                binding.swFirstCond.visibility  = View.VISIBLE
                binding.labFirstCond.visibility = View.VISIBLE
            }
            else  -> {
                binding.swFirstCond.visibility  = View.INVISIBLE
                binding.labFirstCond.visibility = View.INVISIBLE
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun checkData():List<String>{
        val errors = super.checkData() as MutableList<String>

        if(binding.txtDurBlocks.text.toString().toInt() < 5)     errors.add(resources.getString(R.string.select_session))

        val nblocks = binding.txtNBlocks.text.toString().toInt()
        if(nblocks < 2 || ((nblocks % 2) != 0))          errors.add(resources.getString(R.string.warn_blocks))

        return errors
    }
//
//    override fun clear() {
//        super.clear()
//        spinner.setSelection(0)
//    }

    override fun updateSubject(): SubjectRIVGRPParcel{

        subject  = super.updateSubject()

        (subject as SubjectRIVGRPParcel).blockDuration  = binding.txtDurBlocks.text.toString().toLong() * 1000
        (subject as SubjectRIVGRPParcel).rivFirst       = binding.swFirstCond.isChecked
        (subject as SubjectRIVGRPParcel).totBlocks      = binding.txtNBlocks.text.toString().toInt()

        return subject as SubjectRIVGRPParcel
    }
}