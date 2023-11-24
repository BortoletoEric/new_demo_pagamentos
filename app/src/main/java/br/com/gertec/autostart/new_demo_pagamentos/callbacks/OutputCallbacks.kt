package br.com.gertec.autostart.new_demo_pagamentos.callbacks

import android.util.Log
import android.widget.Toast
import br.com.gertec.ppcomp.IPPCompDSPCallbacks
import java.util.concurrent.atomic.AtomicLong

class OutputCallbacks: IPPCompDSPCallbacks {
    override fun Clear() {
        Unit
    }

    override fun Text(p0: Long, p1: String?, p2: String?) {
        Unit
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