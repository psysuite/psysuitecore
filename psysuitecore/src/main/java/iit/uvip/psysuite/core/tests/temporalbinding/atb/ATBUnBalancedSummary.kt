package iit.uvip.psysuite.core.tests.temporalbinding.atb

import android.content.Context
import iit.uvip.psysuite.core.common.Summary
import iit.uvip.psysuite.core.common.TrialBasic
import iit.uvip.psysuite.core.tests.temporalbinding.TrialBindingsUnBalanced
import iit.uvip.psysuite.core.tests.temporalbinding.atb.TestATB.Companion.TYPE_A
import iit.uvip.psysuite.core.tests.temporalbinding.atb.TestATB.Companion.TYPE_AT
import iit.uvip.psysuite.core.tests.temporalbinding.atb.TestATB.Companion.TYPE_A_T
import iit.uvip.psysuite.core.tests.temporalbinding.atb.TestATB.Companion.TYPE_T
import iit.uvip.psysuite.core.tests.temporalbinding.atb.TestATB.Companion.TYPE_T_A
import kotlin.math.roundToInt


class ATBUnBalancedSummary(ctx:Context) : Summary(ctx){

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

        private var latencies:MutableList<ATBsummaryRow> = mutableListOf(
                ATBsummaryRow(TYPE_A,  "A",0),
                ATBsummaryRow(TYPE_A_T,"A_T", 800),
                ATBsummaryRow(TYPE_A_T,"A_T",400),
                ATBsummaryRow(TYPE_A_T,"A_T",300),
                ATBsummaryRow(TYPE_A_T,"A_T",200),
                ATBsummaryRow(TYPE_A_T,"A_T",100),
                ATBsummaryRow(TYPE_AT, "AT",0),
                ATBsummaryRow(TYPE_T_A,"T_A",100),
                ATBsummaryRow(TYPE_T_A,"T_A",200),
                ATBsummaryRow(TYPE_T_A,"T_A",300),
                ATBsummaryRow(TYPE_T_A,"T_A",400),
                ATBsummaryRow(TYPE_T_A,"T_A",800),
                ATBsummaryRow(TYPE_T,  "T",0))

        fun add(trial: TrialBindingsUnBalanced){
            when(trial.type){
                TYPE_A          ->   latencies[0].add(trial)
                TYPE_A_T        -> {
                    when(trial.delay){
                        800L    -> latencies[1].add(trial)
                        400L    -> latencies[2].add(trial)
                        300L    -> latencies[3].add(trial)
                        200L    -> latencies[4].add(trial)
                        100L    -> latencies[5].add(trial)
                    }
                }
                TYPE_AT         ->  latencies[6].add(trial)
                TYPE_T_A        -> {
                    when(trial.delay){
                        100L    -> latencies[7].add(trial)
                        200L    -> latencies[8].add(trial)
                        300L    -> latencies[9].add(trial)
                        400L    -> latencies[10].add(trial)
                        800L    -> latencies[11].add(trial)
                    }
                }
                TYPE_T          ->  latencies[12].add(trial)
            }
        }

        fun close(){
            latencies.map{
                it.close()
            }
        }

        override fun toString():String{
            var res = "type\tlatency\tntrial\t%yes\t%succ\n"
            latencies.map{
                res += it.toString()
            }
            return res
        }
    }

    // type is one of those defined in TestATVB::mTrial
    inner class ATBsummaryRow(val type:Int, val label:String, val latency:Int){

        private var ntrial:Int              = 0
        private var perc_discrimination:Int = 0
        private var rt:Int                  = 0
        private var perc_succ:Int           = 0

        fun add(trial: TrialBindingsUnBalanced){
            ntrial++
            rt += trial.elapsed
            if(trial.success)   perc_succ++

            if((trial.type == TYPE_AT && trial.success) || (trial.type != TYPE_AT && !trial.success)) perc_discrimination++
        }

        fun close(){

            if(ntrial > 0){
                rt                  = ((rt*1F)/ntrial).roundToInt()
                perc_succ           = (((perc_succ*1F)/ntrial)*100F).roundToInt()
                perc_discrimination = (((perc_discrimination*1F)/ntrial)*100F).roundToInt()
            }
        }

        override fun toString():String{
            return "$label\t$latency\t$ntrial\t$perc_discrimination\t$perc_succ\n"
        }
    }
}