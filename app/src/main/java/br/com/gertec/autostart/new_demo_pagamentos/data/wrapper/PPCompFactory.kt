package br.com.gertec.autostart.new_demo_pagamentos.data.wrapper

import android.os.Build
import android.util.Log
import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig
import br.com.gertec.autostart.new_demo_pagamentos.data.devices.PPCompCommands720
import br.com.gertec.autostart.new_demo_pagamentos.data.devices.PPCompCommands780

object PPCompFactory {
    private var instance: PPCompWrapper? = null

    fun getInstance(): PPCompWrapper {
        instance = when (BuildConfig.FLAVOR) {
            "gpos780" -> {
                Log.i("PPCompFactory", "gpos780")
                PPCompCommands780()
            }
            "gpos720" -> {
                Log.i("PPCompFactory", "gpos720")
                PPCompCommands720()
            }
            else -> throw IllegalArgumentException("Dispositivo desconhecido")
        }
        return instance ?: throw IllegalStateException("PPCompFactory não foi inicializada")
    }
}