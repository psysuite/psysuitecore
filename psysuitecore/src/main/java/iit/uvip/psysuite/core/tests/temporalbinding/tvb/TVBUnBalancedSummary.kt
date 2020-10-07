package iit.uvip.psysuite.core.tests.temporalbinding.tvb

import android.content.Context
import iit.uvip.psysuite.core.common.Summary
import iit.uvip.psysuite.core.common.TrialBasic
import iit.uvip.psysuite.core.tests.temporalbinding.TrialBindingsUnBalanced
import iit.uvip.psysuite.core.tests.temporalbinding.atb.TestATB.Companion.TYPE_T
import iit.uvip.psysuite.core.tests.temporalbinding.tvb.TestTVB.Companion.TYPE_TV
import iit.uvip.psysuite.core.tests.temporalbinding.tvb.TestTVB.Companion.TYPE_T_V
import iit.uvip.psysuite.core.tests.temporalbinding.tvb.TestTVB.Companion.TYPE_V
import iit.uvip.psysuite.core.tests.temporalbinding.tvb.TestTVB.Companion.TYPE_V_T
import kotlin.math.roundToInt


class TVBUnBalancedSummary(ctx:Context) : Summary(ctx){

    private var condition:ATBsummaryCondition = ATBsummaryCondition()
    private var summary:String = ""

    // after each trial, filled (with response and success) trial is added to summary
    override fun add(trial: TrialBasic){
        condition.add(trial as TrialBindingsUnBalanced)
    }

    override fun close(filename:String, dir:String):String{
        condition.close()
        summary = condition.toString()
        return writeFile(summary, filename, dir)
    }

    // type is one of those defined in ATVBUnBalancedSummary
    inner class ATBsummaryCondition(){

        private var latencies:MutableList<TVBsummaryRow> = mutableListOf(
                TVBsummaryRow(TYPE_T,  "A",0),
                TVBsummaryRow(TYPE_T_V,"A_V", 800),
                TVBsummaryRow(TYPE_T_V,"A_V",400),
                TVBsummaryRow(TYPE_T_V,"A_V",300),
                TVBsummaryRow(TYPE_T_V,"A_V",200),
                TVBsummaryRow(TYPE_T_V,"A_V",100),
                TVBsummaryRow(TYPE_TV, "AT",0),
                TVBsummaryRow(TYPE_V_T,"T_T",100),
                TVBsummaryRow(TYPE_V_T,"T_T",200),
                TVBsummaryRow(TYPE_V_T,"T_T",300),
                TVBsummaryRow(TYPE_V_T,"T_T",400),
                TVBsummaryRow(TYPE_V_T,"T_T",800),
                TVBsummaryRow(TYPE_V,  "T",0))

        fun add(trial: TrialBindingsUnBalanced){
            when(trial.type){
                TYPE_T          ->   latencies[0].add(trial)
                TYPE_T_V        -> {
                    when(trial.delay){
                        800L    -> latencies[1].add(trial)
                        400L    -> latencies[2].add(trial)
                        300L    -> latencies[3].add(trial)
                        200L    -> latencies[4].add(trial)
                        100L    -> latencies[5].add(trial)
                    }
                }
                TYPE_TV         ->  latencies[6].add(trial)
                TYPE_V_T        -> {
                    when(trial.delay){
                        100L    -> latencies[7].add(trial)
                        200L    -> latencies[8].add(trial)
                        300L    -> latencies[9].add(trial)
                        400L    -> latencies[10].add(trial)
                        800L    -> latencies[11].add(trial)
                    }
                }
                TYPE_V          ->  latencies[12].add(trial)
            }
        }

        fun close(){
            latencies.map{
                it.close()
            }
        }

        override fun toString():String{
            var res = "type\tlat\tntr\t%yes\t%succ\trt\n"
            latencies.map{
                res += it.toString()
            }
            return res
        }
    }

    // type is one of those defined in TestATVB::mTrial
    inner class TVBsummaryRow(val type:Int, val label:String, val latency:Int){

        private var ntrial:Int              = 0
        private var perc_discrimination:Int = 0
        private var rt:Int                  = 0
        private var perc_succ:Int           = 0

        fun add(trial: TrialBindingsUnBalanced){
            ntrial++
            rt += trial.elapsed
            if(trial.success)   perc_succ++

            if((trial.type == TYPE_TV && trial.success) || (trial.type != TYPE_TV && !trial.success)) perc_discrimination++
        }

        fun close(){

            if(ntrial > 0){
                rt                  = ((rt*1F)/ntrial).roundToInt()
                perc_succ           = (((perc_succ*1F)/ntrial)*100F).roundToInt()
                perc_discrimination = (((perc_discrimination*1F)/ntrial)*100F).roundToInt()
            }
        }

        override fun toString():String{
            return "$label\t$latency\t$ntrial\t$perc_discrimination\t$perc_succ\t$rt\n"
        }
    }
}