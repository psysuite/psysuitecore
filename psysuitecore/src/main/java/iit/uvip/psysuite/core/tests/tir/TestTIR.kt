package iit.uvip.psysuite.core.tests.tir

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import iit.uvip.psysuite.core.model.parcel.SubjectBasicParcel
import iit.uvip.psysuite.core.tests.TestBasic
import iit.uvip.psysuite.core.trials.TrialBasic
import org.albaspazio.core.accessory.VibrationManager
import org.albaspazio.core.speech.SpeechManager


class TestTIR(ctx: Context,
              activity: Activity,
              hostfragment: Fragment,
              subject: SubjectBasicParcel,
              vibrator: VibrationManager?,
              mImageView: ImageView?,
              speechManager: SpeechManager?,
              private val mainView: View
) : TestBasic(ctx, activity, hostfragment, subject, vibrator, mImageView)  {

    override fun initTest(){

    }

    override fun show(trial: TrialBasic, isRepeat: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onTrialEnd() {
        TODO("Not yet implemented")
    }

    override fun initSummary() {
        TODO("Not yet implemented")
    }
}