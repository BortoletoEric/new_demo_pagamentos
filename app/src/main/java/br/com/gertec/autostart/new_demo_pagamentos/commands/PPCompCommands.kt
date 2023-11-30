package br.com.gertec.autostart.new_demo_pagamentos.commands

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Button
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.model.Tabelas
import br.com.gertec.ppcomp.IPPCompDSPCallbacks
import br.com.gertec.ppcomp.PPComp
import br.com.gertec.ppcomp.exceptions.PPCompDumbCardException
import br.com.gertec.ppcomp.exceptions.PPCompMCDataErrException
import br.com.gertec.ppcomp.exceptions.PPCompNoCardException
import br.com.gertec.ppcomp.exceptions.PPCompProcessingException
import br.com.gertec.ppcomp.exceptions.PPCompTabExpException
import br.com.gertec.ppcomp.exceptions.PPCompTimeoutException

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

    fun init(context: Context){
        ppComp = PPComp(context)
    }

    fun open(){
        ppComp?.PP_Open()
    }
    fun setDspCallback(outputCallbacks: IPPCompDSPCallbacks){
        ppComp?.PP_SetDspCallbacks(outputCallbacks)
    }

    fun close(message: String){
        ppComp?.PP_Close(message)
    }

    fun abort(){
        Log.d("msgg","ABORT!")
        cancelCheckEvent = true
        try {
            ppComp?.PP_Abort()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun checkEvent(input: String): String?{
        try{
            Log.d("msgg","CKE init...")
            ppComp?.PP_StartCheckEvent(input)
            while (true) {
//                if (cancelCheckEvent) {
//                    ppComp?.PP_Abort()
//                    cancelCheckEvent = false
//                    throw PPCompCancelException()
//                }
                try {
                    var resp = ppComp?.PP_CheckEvent()
                    Log.d("msgg","CKE OK $resp")
                    return resp
                } catch (e: PPCompProcessingException) {
                    e.printStackTrace()
                }
            }
        } catch (e: PPCompMCDataErrException){
            Log.d("msgg","CKE EXC $e")
            e.printStackTrace()
            return "CKE_MC_ERR"
        }catch (e: Exception){
            Log.d("msgg","CKE EXC $e")
            e.printStackTrace()
            return ""
        }
    }

    fun getCard(input: String): Pair<Boolean,String?>{
        var gcrOut: String?
        try{
            Log.d("msgg","GCR init...")
            ppComp?.PP_StartGetCard(input)

            while(true){
                try{
                    Log.d("msgg","GCR init 2...")
                    gcrOut = ppComp?.PP_GetCard()
                    Log.d("msgg","GCR ok")
                    return Pair(true,gcrOut)
                } catch(e: PPCompTabExpException){
                    if(tableLoad("001357997531")){  // se nao der, tenta esse kk 010123456789
                        try{
                            ppComp?.PP_ResumeGetCard()
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                } catch(e: PPCompDumbCardException){
                    Log.d("msgg","DUMB EXC? $e")
                    try{
                        ppComp?.PP_StartRemoveCard("RETIRE O CARTAO")
                        ppComp?.PP_RemoveCard()
                        Log.i("GRANDE SUSTO", "ABORT 5")
                        ppComp?.PP_Abort()
                        return Pair(false,e.toString())
                    }catch (e: Exception){
                        e.printStackTrace()
                        return Pair(false,e.toString())
                    }
                } catch (e: Exception){
                    Log.d("msgg","GCR OTHER ERR $e")
                    e.printStackTrace()
                    return Pair(false,e.toString())
                }
            }
        }catch(e: Exception){
            Log.i("GRANDE SUSTO", "ABORT 4")
            ppComp?.PP_Abort()
            Log.d("msgg","START GCR ERROR $e")
            return Pair(false,e.toString())
        }
    }

    private fun tableLoad(input: String): Boolean {
        try {
            ppComp?.PP_TableLoadInit(input)
            return true
        }catch(e: Exception){
            e.printStackTrace()
            try {
                tabelas.aidsList.forEach{ aid ->
                    ppComp?.PP_TableLoadRec("01$aid")
                }
                tabelas.capksList.forEach{ capk ->
                    ppComp?.PP_TableLoadRec("01$capk")
                }
                ppComp?.PP_TableLoadEnd()
                return true
            }catch (e:Exception){
                e.printStackTrace()
                return false
            }
        }
    }

    fun goOnChip(input: String, tags: String? = null, opTags: String? = null): String?{
        try {
            if(tags.isNullOrEmpty() || opTags.isNullOrEmpty()){
                ppComp?.PP_StartGoOnChip(input)
            }else{
                ppComp?.PP_StartGoOnChip(input, tags, opTags)
            }
            while(true){
                try{
                    val g = ppComp?.PP_GoOnChip()
                    Log.d("msgg","GOC OK ")
                    return g
                }catch(e: PPCompNoCardException){
                    cancelCheckEvent = false
                    e.printStackTrace()
                    return "GOC_NO_CARD"
                }catch(e: PPCompTimeoutException){
                    cancelCheckEvent = false
                    Log.d("msgg","GOC EXCto --> $e ")
                    try {
                        ppComp?.PP_StartRemoveCard("RETIRE O CARTAO")
                        ppComp?.PP_RemoveCard()
                        Log.i("GRANDE SUSTO", "ABORT 3")
                        ppComp?.PP_Abort()
                        return "GOC_TO"
                    }catch (e: Exception){ e.printStackTrace() }
                    e.printStackTrace()
                }catch(e: Exception){
                    cancelCheckEvent = false
                    Log.d("msgg","GOC EXC --> $e ")
                    try {
                        ppComp?.PP_StartRemoveCard("RETIRE O CARTAO")
                        ppComp?.PP_RemoveCard()
                        Log.i("GRANDE SUSTO", "ABORT 2")
                        ppComp?.PP_Abort()
                        return ""
                    }catch (e: Exception){ e.printStackTrace() }
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            cancelCheckEvent = false
            try {
                ppComp?.PP_RemoveCard()
                Log.i("GRANDE SUSTO", "ABORT 1")
                ppComp?.PP_Abort()
            }catch (e: Exception){ e.printStackTrace() }
            return ""
        }
    }

    fun finishChip(){
        try {
            ppComp?.PP_FinishChip("0000000000","011829F279F269F36958F9F37")
            removeCard(" ")
        }catch (e: Exception){e.printStackTrace()}
    }

    fun setPinKeyboard(
        b1: Button, b2: Button,
        b3: Button, b4: Button,
        b5: Button, b6: Button,
        b7: Button, b8: Button,
        b9: Button, b0: Button,
        bCancel: Button, bConfirm: Button,
        bClear: Button, activity: Activity, beep: Boolean
    ){
        ppComp?.PP_SetKbd(b1,b2,b3,b4,b5,b6,b7,b8,b9,b0,bCancel, bConfirm, bClear, activity, beep)
    }
//    fun getTagsString(input: String): String {
//        return String.format("%03d", tags.length / 2) + tags
//    }
//
//    fun getTagsOpString(input: String): String {
//        return String.format("%03d", tagsOpt.length / 2) + tagsOpt
//    }

    fun removeCard(input: String){
        try{
            ppComp?.PP_StartRemoveCard(input)
            ppComp?.PP_RemoveCard()
        }catch(e: Exception){
            e.printStackTrace()
        }

    }

}