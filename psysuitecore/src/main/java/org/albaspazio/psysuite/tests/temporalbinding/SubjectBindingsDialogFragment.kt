package org.albaspazio.psysuite.tests.temporalbinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import org.albaspazio.psysuite.core.R
import org.albaspazio.psysuite.core.databinding.FragmentSubjectInfoBasicBinding
import org.albaspazio.psysuite.model.SubjectBasicParcel
import org.albaspazio.psysuite.tests.TestBasic
import org.albaspazio.psysuite.tests.TestBasic.Companion.TEST_ATB_TIME_INF
import org.albaspazio.psysuite.tests.TestBasic.Companion.TEST_AVB_TIME_INF
import org.albaspazio.psysuite.tests.TestBasic.Companion.TEST_TVB_TIME_INF
import org.albaspazio.psysuite.ui.SubjectBasicDialogFragment
import org.albaspazio.psysuite.utility.ConditionData

// add whitenoise check button
class SubjectBindingsDialogFragment : SubjectBasicDialogFragment(), AdapterView.OnItemSelectedListener
{
    override val LOG_TAG: String = SubjectBindingsDialogFragment::class.java.simpleName

    private lateinit var binding: FragmentSubjectInfoBasicBinding

    private lateinit var mTaskCodeLabelsADA: MutableList<ConditionData>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_subject_info_basic, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSubjectInfoBasicBinding.bind(mView)
        super.onViewCreated(view, savedInstanceState)
    }

    // create the list of available condition when using adapative trials (all but infants ones)
    override fun initData() {
        super.initData()
        mTaskCodeLabelsADA = mutableListOf()
        for(cond in mTaskCodeLabels){
            if (cond.id != TEST_ATB_TIME_INF && cond.id != TEST_TVB_TIME_INF && cond.id != TEST_AVB_TIME_INF)
                mTaskCodeLabelsADA.add(cond)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

        when((binding.spCondition.selectedItem as ConditionData).id){

            TEST_TVB_TIME_INF,
            TEST_ATB_TIME_INF,
            TEST_AVB_TIME_INF -> {
                binding.swInteractive.visibility = View.VISIBLE
                if (subject.nextTrailModality == TestBasic.TEST_NEXTTRIAL_AUTO || subject.nextTrailModality == TestBasic.TEST_NEXTTRIAL_BUTTON) {
                    binding.swInteractive.isSelected = false
                    subject.nextTrailModality = TestBasic.TEST_NEXTTRIAL_AUTO
                }
            }
            else -> binding.swInteractive.visibility = View.GONE
        }
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun updateSubject(): SubjectBasicParcel {
        subject = super.updateSubject()

        subject.nextTrailModality = when(subject.type) {                // could choose whether pausing each trial
            TEST_AVB_TIME_INF,
            TEST_TVB_TIME_INF,
            TEST_ATB_TIME_INF         ->  if(binding.swInteractive.isSelected)  TestBasic.TEST_NEXTTRIAL_BUTTON
                                          else                                  TestBasic.TEST_NEXTTRIAL_AUTO
            else                      ->                                        subject.nextTrailModality
        }


        if(subject.type == TestBasic.TEST_ATVB_TIME_S_BAL || subject.type == TestBasic.TEST_ATVB_TIME_S_BAL2)
            subject.classes         = listOf("org.albaspazio.psysuite.tests.temporalbinding.atvb.TestATVB",
                                             "org.albaspazio.psysuite.ui.fragments.answers.ThreeAFCAnswerDialogFragment")

        return subject
    }

    override fun setTrialManager(selManager:Any){
        if(selManager as String == resources.getString(R.string.trials_adaptive))
                setConditions(mTaskCodeLabelsADA)
        else    setConditions(mTaskCodeLabels)
    }
}