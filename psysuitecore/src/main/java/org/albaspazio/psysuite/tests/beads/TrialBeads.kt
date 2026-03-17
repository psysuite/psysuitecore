package org.albaspazio.psysuite.tests.beads

import org.albaspazio.psysuite.trials.TrialBasic

//                trial_id    0/1      fig_res
class TrialBeads(id: Int = -1, type: Int, label: String, var img_res:Int, var beads_types:List<Boolean>) :
    TrialBasic(id, type, label) {

    companion object {
        @JvmStatic val LOG_HEADER           = "id\ttype\tfig\n"
    }

    // only to validate Class
    override fun Log():String{
        return "$id\t$type\t$label\n"
    }
}
