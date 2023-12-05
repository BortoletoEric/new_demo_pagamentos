package br.com.gertec.autostart.new_demo_pagamentos.callbacks

import android.util.Log
import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.PinKbdActivity
import br.com.gertec.ppcomp.IPPCompDSPCallbacks
import java.util.concurrent.atomic.AtomicLong

class OutputCallbacks(private val mainActivity: MainActivity): IPPCompDSPCallbacks {
    override fun Clear() {
        Unit
    }

    override fun Text(lFlags: Long, p1: String?, p2: String?) {
        Log.d("msgg","($lFlags)OUT: $p1\n$p2")

        mainActivity.mainViewModel.updateDisplay(lFlags, p1?:"", p2?:"")

//        if(lFlags == 917504L){
//            Log.d("msgg", "flag setkbd init...")
//            if (BuildConfig.FLAVOR == "gpos760") return
//            mainActivity.mainViewModel.ppCompCommands.showKBD(PinKbdActivity(), mainActivity)
//        }

    }

    override fun MenuStart(p0: String?, p1: AtomicLong?): Int {
        Unit
        return 0
    }

    override fun MenuShow(p0: Long, p1: MutableList<String>?, p2: Int): Int {
        p1?.forEach {
            Unit
        }
        return 0
    }

}