package iit.uvip.psysuite.core.trials

abstract class TrialBasic(var id:Int=-1, val type:Int, protected val label:String="", open var stim_value:Long=-999999999L, var correct_answer:Int=0, var variable_param:Any? = null) {

    val UNSET_VALUE:Long = -999999999L

    var user_answer:Int             = -1
    var repetitions:Int             =  1
    var elapsed:Int                 = -1
    var user_answer_extra:String    = ""

    var success:Boolean     =  false    // result of comparison between correct and user answer

    // data exported to log file
    abstract fun Log():String

    open fun debugInfo():String{
        return "lab=$label, type=$type, stim_value=$stim_value, corr_answ=$correct_answer"
    }

    open fun setResponse(result: Int, elapsedms: Int, extra_text:String = "") {
        user_answer         = result
        elapsed             = elapsedms
        user_answer_extra   = extra_text
        success             = (result == correct_answer)
    }

    // this has been added to manage adaptive trials after new value has been defined. must be implemented in each Test
    open fun updateTrial(newvalue:Float){}
}



