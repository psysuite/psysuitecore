package iit.uvip.psysuite.core.ui.fragments.answers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import iit.uvip.psysuite.core.R
import kotlinx.android.synthetic.main.fragment_3afc_answer.*
import org.albaspazio.core.speech.SpeechManager

class ThreeAFCAnswerDialogFragment: TwoAFCAnswerDialogFragment() {

    override val LOG_TAG = ThreeAFCAnswerDialogFragment::class.java.simpleName


    companion object {
        fun newInstance(title: String, speechManager: SpeechManager): ThreeAFCAnswerDialogFragment {
            val frag = ThreeAFCAnswerDialogFragment()
            val args = Bundle()
            args.putString("title", title)
            frag.setArguments(args)
            frag.tts = speechManager

            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_3afc_answer, container)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rb_a_2.text = mAnswers[2]
    }
}