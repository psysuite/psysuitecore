package iit.uvip.psysuite.core.tests.tid

import iit.uvip.psysuite.core.common.TestBasic
import iit.uvip.psysuite.core.common.subjects_parcel.SubjectLongitParcel
import kotlinx.android.parcel.Parcelize
import org.albaspazio.core.accessory.Device
import org.albaspazio.core.accessory.getDateString

// session
@Parcelize
class SubjectTIDParcel(
    override var type: Int = -1,
    override var label: String = "",
    override var age: Int = -1,
    override var gender: Int = -1,
    override var nextTrailModality: Int = -1,
    override var canRecordAudio:Boolean = false,
    override var testClass:String = "",
    override var device: Device? = null,
    override var block:Int = -1,

    override var spinner_sel: Int = -1,
    override var spinner_data_resource: Int = -1,
    var group: Int = -1
) : SubjectLongitParcel(type, label, age, gender, nextTrailModality, canRecordAudio, testClass, device, block, spinner_sel, spinner_data_resource){

    override fun getFilesPrefix():String = "${label}_${group}_s${session}_${type}"

    override fun composeSubjectFileName(blk:Int):String{
        if(label.isBlank() || group == -1 || type == -1 || session == -1)   return ""

        val blkstr =    if(blk > -1)    "_blk$blk"
                        else           ""

        return "${getFilesPrefix()}_${getDateString()}${blkstr}${TestBasic.FILE_EXTENSION}"
    }
}












