package iit.uvip.psysuite.core.tests.tir

import iit.uvip.psysuite.core.trials.TrialBasic

class TrialTIR (id:Int=-1, type:Int, label:String,
                override var magnitude:Float,
                time:Long,
                distance:Int,
                val minMagnitude:Float,
                isADA:Boolean=false): TrialBasic(id, type, label, isADA=false) {

}