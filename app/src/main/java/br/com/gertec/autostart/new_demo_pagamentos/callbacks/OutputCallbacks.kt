package br.com.gertec.autostart.new_demo_pagamentos.callbacks

import android.util.Log
import android.widget.Toast
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.ppcomp.IPPCompDSPCallbacks
import java.util.concurrent.atomic.AtomicLong

class OutputCallbacks(private val mainActivity: MainActivity): IPPCompDSPCallbacks {
    override fun Clear() {
        Unit
    }

    override fun Text(lFlags: Long, p1: String?, p2: String?) {
        Log.d("msgg","($lFlags)OUT: $p1\n$p2")

        mainActivity.mainViewModel.updateDisplay(lFlags, p1?:"", p2?:"")
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