package br.com.gertec.autostart.new_demo_pagamentos.devices.wrapper

import android.util.Log
import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig
import br.com.gertec.autostart.new_demo_pagamentos.devices.gpos720.PPCompCommands as PPCompCommands720
import br.com.gertec.autostart.new_demo_pagamentos.devices.gpos760.PPCompCommands as PPCompCommands760
import br.com.gertec.autostart.new_demo_pagamentos.devices.gpos780.PPCompCommands as PPCompCommands780


object PPCompFactory {
    private var instance: PPCompWrapper? = null

    fun getInstance(): PPCompWrapper {
        instance = when (BuildConfig.FLAVOR) {
            "gpos780" -> {
                Log.i("PPCompFactory", "gpos780")
                PPCompCommands720()
            }
            "gpos720" -> {
                Log.i("PPCompFactory", "gpos720")
                PPCompCommands760()
            }
            "gpos760" -> {
                Log.i("PPCompFactory", "gpos760")
                PPCompCommands780()
            }
            else -> throw IllegalArgumentException("Dispositivo desconhecido")
        }
        return instance ?: throw IllegalStateException("PPCompFactory não foi inicializada")
    }
}