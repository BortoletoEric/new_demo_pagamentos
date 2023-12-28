package br.com.gertec.autostart.new_demo_pagamentos.commands

import android.content.Context
import android.os.Build
import android.util.Log
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.PinKbdActivity
import br.com.gertec.autostart.new_demo_pagamentos.model.KBDData
import br.com.gertec.autostart.new_demo_pagamentos.model.Tabelas
import br.com.gertec.ppcomp.IPPCompDSPCallbacks
import br.com.gertec.ppcomp.PPComp
import br.com.gertec.ppcomp.enums.LANGUAGE
import br.com.gertec.ppcomp.exceptions.PPCompDumbCardException
import br.com.gertec.ppcomp.exceptions.PPCompMCDataErrException
import br.com.gertec.ppcomp.exceptions.PPCompNoCardException
import br.com.gertec.ppcomp.exceptions.PPCompProcessingException
import br.com.gertec.ppcomp.exceptions.PPCompTabExpException
import br.com.gertec.ppcomp.exceptions.PPCompTimeoutException
import java.util.Locale

class PPCompCommands private constructor() {
    private var ppComp: PPComp? = null
    private var cancelCheckEvent = false
    private val tabelas = Tabelas()

    companion object {
        private var instance: PPCompCommands? = null

        // Método para obter a instância única da classe
        fun getInstance(): PPCompCommands {
            if (instance == null) {
                instance = PPCompCommands()
            }
            return instance!!
        }
    }

    fun init(context: Context) {
        ppComp = PPComp(context)
    }

    fun open() {
        try {
            ppComp?.PP_Open()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setDspCallback(outputCallbacks: IPPCompDSPCallbacks) {
        ppComp?.PP_SetDspCallbacks(outputCallbacks)
    }

    fun checkEvent(input: String): String? {
        try {
            ppComp?.PP_StartCheckEvent(input)
            while (true) {
                try {
                    return ppComp?.PP_CheckEvent()
                } catch (e: PPCompProcessingException) {
                    e.printStackTrace()
                }
            }
        } catch (e: PPCompMCDataErrException) {
            e.printStackTrace()
            return "CKE_MC_ERR"
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun getCard(input: String, context: Context): Pair<Boolean, String?> {
        var gcrOut: String?

        try {
            ppComp?.PP_StartGetCard(input)
            while (true) {
                try {
                    gcrOut = ppComp?.PP_GetCard()
                    return Pair(true, gcrOut)
                } catch (e: PPCompTabExpException) {
                    if (tableLoad(
                            "010123456789",
                            context
                        )
                    ) {  // se nao der, tenta esse kk   011357997531
                        try {
                            ppComp?.PP_ResumeGetCard()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: PPCompDumbCardException) {
                    try {
                        ppComp?.PP_StartRemoveCard(context.getString(R.string.retire_o_cartao))
                        ppComp?.PP_RemoveCard()
                        ppComp?.PP_Abort()
                        return Pair(false, e.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return Pair(false, e.toString())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return Pair(false, e.toString())
                }
            }
        } catch (e: Exception) {
            ppComp?.PP_Abort()
            return Pair(false, e.toString())
        }
    }

    fun goOnChip(
        input: String,
        tags: String? = null,
        opTags: String? = null,
        context: Context
    ): Pair<String?, String?> { //Pair: resposta, msg da resposta
        try {
            Log.d("msgg","init GOC...")
            ppComp?.PP_StartGoOnChip(input, tags, opTags)

            while (true) {
                try {
                    val g = ppComp?.PP_GoOnChip()
                    PinKbdActivity.kBDData?.activity?.finish()
                    Log.d("msgg","GOC RESP: $g")
                    return Pair(g, "")
                } catch (e: PPCompNoCardException) {
                    cancelCheckEvent = false
                    PinKbdActivity.kBDData?.activity?.finish()
                    e.printStackTrace()
                    return Pair("GOC_NO_CARD", "")
                } catch (e: PPCompTimeoutException) {
                    cancelCheckEvent = false
                    PinKbdActivity.kBDData?.activity?.finish()
                    try {
                        ppComp?.PP_StartRemoveCard(context.getString(R.string.retire_o_cartao))
                        ppComp?.PP_RemoveCard()
                        ppComp?.PP_Abort()
                        return Pair("GOC_TO", "")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    e.printStackTrace()
                } catch (e: Exception) {
                    cancelCheckEvent = false
                    PinKbdActivity.kBDData?.activity?.finish()
                    e.printStackTrace()
                    try {
                        ppComp?.PP_StartRemoveCard(context.getString(R.string.retire_o_cartao))
                        ppComp?.PP_RemoveCard()
                        ppComp?.PP_Abort()
                        Log.d("msgg","GOC_ERR: $e")
                        return Pair("GOC_ERR", "$e")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return Pair("GOC_ERR", "$e")
                    }
                }catch (e: Exception) {
                    cancelCheckEvent = false
                    PinKbdActivity.kBDData?.activity?.finish()
                    e.printStackTrace()
                    try {
                        ppComp?.PP_StartRemoveCard(context.getString(R.string.retire_o_cartao))
                        ppComp?.PP_RemoveCard()
                        ppComp?.PP_Abort()
                        Log.d("msgg","GOC_ERR: $e")
                        return Pair("GOC_ERR", "$e")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return Pair("GOC_ERR", "$e")
                    }
                }
            }
        } catch (e: Exception) {
            cancelCheckEvent = false
            PinKbdActivity.kBDData?.activity?.finish()
            try {
                ppComp?.PP_StartRemoveCard(context.getString(R.string.retire_o_cartao))
                ppComp?.PP_RemoveCard()
                ppComp?.PP_Abort()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return Pair("", "")
        }
    }

    fun setKbd() {
        val kbdData: KBDData? = PinKbdActivity.kBDData
        ppComp?.PP_SetKbd(
            kbdData?.btn1,
            kbdData?.btn2,
            kbdData?.btn3,
            kbdData?.btn4,
            kbdData?.btn5,
            kbdData?.btn6,
            kbdData?.btn7,
            kbdData?.btn8,
            kbdData?.btn9,
            kbdData?.btn0,
            kbdData?.btnCancel,
            kbdData?.btnConfirm,
            kbdData?.btnClear,
            kbdData?.activity
        )
    }

    fun finishChip() {
        Log.d("msgg","init FNC...")
        try {
            ppComp?.PP_FinishChip("0000000000", "011829F279F269F36958F9F37")
            removeCard()
        } catch (e: Exception) {
            Log.d("msgg","erro FNC $e")
            e.printStackTrace()
        }
    }

    private fun removeCard() {
        try {
            ppComp?.PP_StartRemoveCard(" ")
            ppComp?.PP_RemoveCard()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun tableLoad(input: String, context: Context): Boolean {
        try {
            ppComp?.PP_TableLoadInit(input)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                if (getDeviceLanguage(context) == "pt") {
                    tabelas.aidsList.forEach { aid ->
                        ppComp?.PP_TableLoadRec("01$aid")
                    }
                } else {
                    tabelas.aidsListEnglish.forEach { aid ->
                        ppComp?.PP_TableLoadRec("01$aid")
                    }
                }
                tabelas.capksList.forEach { capk ->
                    ppComp?.PP_TableLoadRec("01$capk")
                }
                ppComp?.PP_TableLoadEnd()
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
    }

    fun selectLanguage(deviceLanguage: String) {
        if (deviceLanguage == "pt") ppComp?.PP_SelectLanguage(LANGUAGE.PT) else ppComp?.PP_SelectLanguage(
            LANGUAGE.EN
        )
    }

    private fun getDeviceLanguage(context: Context): String {
        val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        return locale.language
    }

    fun abort() {
        cancelCheckEvent = true
        try {
            ppComp?.PP_Abort()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun close(message: String) {
        try {
            ppComp?.PP_Close(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}