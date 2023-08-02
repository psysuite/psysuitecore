package iit.uvip.psysuite.core.tests.bis

import iit.uvip.psysuite.core.trials.TrialsManager.Companion.ADAPTIVE_VALUE

class TrialBISADA(id:Int=-1, type:Int, label:String, corr_answer:Int, isBefore:Boolean, conflict_type:String, duration:Long, duration2:Long=0L, mid_latency:Long = 500L)
     :TrialBIS(id,type,label, corr_answer, ADAPTIVE_VALUE, isBefore, conflict_type, duration, duration2, mid_latency)
