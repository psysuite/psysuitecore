package iit.uvip.psysuite.core.tests.ttc

import iit.uvip.psysuite.core.tests.TestBasic
import iit.uvip.psysuite.core.trials.TrialBasic
import org.albaspazio.core.accessory.round
import kotlin.math.round

// speed in px per second
// distance in px
// magnitude is the distance from correctTime = dist/speed (e.g. 250),
// stim_value is the temporal distance from start (e.g. 1000-magnitude=750ms) when target must disappear (VT)
open class TrialTTC(id:Int=-1, type:Int, label:String,
                    final override var magnitude:Float,
                    time:Long,
                    distance:Int,
                    val imageId:Int, val isHoriz:Boolean=true, val isDownRight:Boolean=true, isADA:Boolean=false): TrialBasic(id, type, label, isADA=isADA) {
    var TT:Long
    var VT:Long
    var IT:Long

    var TPL:Int
    var VPL:Int
    var IPL:Int

    var SP:Double

    private var error:Int = 0

    override val stim_value: Long
        get() = VT

    companion object {
        @JvmStatic val LOG_HEADER = "id\tlabel\tvis_time\tisHor\tis_dr\tres\terror\tspeed\tdist\ttime\timageid\n"
    }

    init {
        when(type){
            TestBasic.TEST_MOTPRE_VH_VARSPEED_FIXVT  -> {
                SP  = magnitude.toDouble()
                VT  = time
                IPL = distance
                VPL = round(SP*VT).toInt()
                IT  = round(IPL/SP).toLong()
                TPL = VPL + IPL
                TT  = VT + IT
            }
            TestBasic.TEST_MOTPRE_VH_VARSPEED_FIXVPL -> {
                SP  = magnitude.toDouble()
                IT  = time
                VPL = distance
                IPL = round(SP*IT).toInt()
                VT  = round(VPL/SP).toLong()
                TPL = VPL + IPL
                TT  = VT + IT
            }
            else -> {    // TestBasic.TEST_MOTPRE_VH_FIXSPEED
                TPL = distance
                TT  = time
                SP  = TPL/TT.toDouble()
                IPL = (magnitude*TPL).toInt()
                IT  = (magnitude*TT).toLong()
                VPL = TPL - IPL
                VT  = TT - IT
            }
        }
        updateTrial(magnitude)
    }
    // all class exported as string
    override fun toString():String{
        return "$id\t$label\t$stim_value\t$isHoriz\t$isDownRight\t$user_answer\t$error\t${SP.round(3)}\t$TPL\t$TT\t$imageId"
    }

    // data exported to log file
    override fun Log():String{
        return toString() + "\n"
    }

    override fun debugInfo():String{
        return "${super.debugInfo()}, pos=$stim_value, is_oriz=$isHoriz, is_down_right=$isDownRight"
    }

    override fun setResponse(result: Int, elapsedms: Int, extra_text:String) {
        super.setResponse(result, elapsedms, extra_text)
        error = (result - TT).toInt()
    }
}
