package iit.uvip.psysuite.core.tests.temporalbinding

import iit.uvip.psysuite.core.tests.temporalbinding.atvb.TestATVB.Companion.TYPE_A_T_V
import iit.uvip.psysuite.core.tests.temporalbinding.atvb.TestATVB.Companion.TYPE_A_V_T
import iit.uvip.psysuite.core.tests.temporalbinding.atvb.TestATVB.Companion.TYPE_T_A_V
import iit.uvip.psysuite.core.tests.temporalbinding.atvb.TestATVB.Companion.TYPE_T_V_A
import iit.uvip.psysuite.core.tests.temporalbinding.atvb.TestATVB.Companion.TYPE_V_A_T
import iit.uvip.psysuite.core.tests.temporalbinding.atvb.TestATVB.Companion.TYPE_V_T_A


class TrialBindingsBalanced(id:Int=-1, type:Int=0, val delay:Long=0L, correct_answers:List<String>):TrialBindings3latencies(id, type, 0,0,0, "") {

    companion object {
        @JvmStatic
        val LOG_HEADER = "id\ttype\tlabel\tdelay\tanswer\tsuccess\telapsed\n"
    }

    init {
        when(type){
            TYPE_T_A_V -> {
                t = 0
                a = delay
                v = delay*2
                correct_answer = correct_answers[0]
            }
            TYPE_V_A_T ->{
                v = 0
                a = delay
                t = delay*2
                correct_answer = correct_answers[0]
            }
            TYPE_A_T_V ->{
                a = 0
                t = delay
                v = delay*2
                correct_answer = correct_answers[1]
            }
            TYPE_V_T_A ->{
                v = 0
                t = delay
                a = delay*2
                correct_answer = correct_answers[1]
            }

            TYPE_A_V_T -> {
                a = 0
                v = delay
                t = delay*2
                correct_answer = correct_answers[2]
            }
            TYPE_T_V_A -> {
                t = 0
                v = delay
                a = delay*2
                correct_answer = correct_answers[2]
            }
        }
    }

    // all class exported as string
    override fun toString(): String {
        return id.toString() + "\t" + type.toString() + "\t" + label + "\t" + delay.toString() + "\n"
    }

    // data exported to log file
    override fun Log(): String {
        return id.toString() + "\t" + type.toString() + "\t" + label + "\t" + delay.toString() + "\t" + user_answer + "\t" + success.toString() + "\t" + elapsed.toString() +"\n"
    }

    override fun debugInfo():String{
        return "${super.debugInfo()}, label=$label, delay=$delay"
    }
}
