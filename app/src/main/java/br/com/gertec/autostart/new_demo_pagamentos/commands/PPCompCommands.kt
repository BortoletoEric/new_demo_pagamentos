package br.com.gertec.autostart.new_demo_pagamentos.commands

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.PinKbdActivity
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
        try {
            ppComp?.PP_Open()
        }catch (e:Exception){e.printStackTrace()}
    }
    fun setDspCallback(outputCallbacks: IPPCompDSPCallbacks){
        ppComp?.PP_SetDspCallbacks(outputCallbacks)
    }

    fun close(message: String){
        try {
            ppComp?.PP_Close(message)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun abort(){
        cancelCheckEvent = true
        try {
            ppComp?.PP_Abort()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun checkEvent(input: String): String?{
        try{
            ppComp?.PP_StartCheckEvent(input)
            while (true) {
//                if (cancelCheckEvent) {
//                    ppComp?.PP_Abort()
//                    cancelCheckEvent = false
//                    throw PPCompCancelException()
//                }
                try {
                    var resp = ppComp?.PP_CheckEvent()
                    return resp
                } catch (e: PPCompProcessingException) {
                    e.printStackTrace()
                }
            }
        } catch (e: PPCompMCDataErrException){
            e.printStackTrace()
            return "CKE_MC_ERR"
        }catch (e: Exception){
            e.printStackTrace()
            return ""
        }
    }

    fun getCard(input: String): Pair<Boolean,String?>{
        var gcrOut: String?
        try{
            ppComp?.PP_StartGetCard(input)
            while(true){
                try{
                    gcrOut = ppComp?.PP_GetCard()

                    Log.d("msgg","GCR RESP: $gcrOut")
                    return Pair(true,gcrOut)
                } catch(e: PPCompTabExpException){
                    if(tableLoad("010123456789")){  // se nao der, tenta esse kk   011357997531
                        try{
                            ppComp?.PP_ResumeGetCard()
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                } catch(e: PPCompDumbCardException){
                    try{
                        ppComp?.PP_StartRemoveCard("RETIRE O CARTAO")
                        ppComp?.PP_RemoveCard()
                        Log.i("msgg", "ABORT 5")
                        ppComp?.PP_Abort()
                        return Pair(false,e.toString())
                    }catch (e: Exception){
                        e.printStackTrace()
                        return Pair(false,e.toString())
                    }
                } catch (e: Exception){
                    e.printStackTrace()
                    return Pair(false,e.toString())
                }
            }
        }catch(e: Exception){
            ppComp?.PP_Abort()
            return Pair(false,e.toString())
        }
    }

    private fun tableLoad(input: String): Boolean {
        try {
            Log.d("msgg","TABLE LOAD")
            ppComp?.PP_TableLoadInit(input)
            Log.d("msgg","TABLE LOAD FINISH")
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

    fun goOnChip(
        input: String,
        tags: String? = null,
        opTags: String? = null,
        requireContext: Context
    ): String?{
        try {

            val intent = Intent(requireContext, PinKbdActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
//            activity.startActivityForResult(intent,0)
                requireContext.startActivity(intent)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

           ppComp?.PP_StartGoOnChip(input, tags, opTags)

            while(true){
                try{
                    val g = ppComp?.PP_GoOnChip()
                    return g
                }catch(e: PPCompNoCardException){
                    cancelCheckEvent = false
                    e.printStackTrace()
                    return "GOC_NO_CARD"
                }catch(e: PPCompTimeoutException){
                    cancelCheckEvent = false
                    try {
                        ppComp?.PP_StartRemoveCard("RETIRE O CARTAO")
                        ppComp?.PP_RemoveCard()
                        Log.i("msgg", "ABORT 3")
                        ppComp?.PP_Abort()
                        return "GOC_TO"
                    }catch (e: Exception){ e.printStackTrace() }
                    e.printStackTrace()
                }
                catch(e: Exception){
                    Log.i("msgg", "GOC EXC: $e")
                    cancelCheckEvent = false
                    try {
                        ppComp?.PP_StartRemoveCard("RETIRE O CARTAO")
                        ppComp?.PP_RemoveCard()
                        Log.i("msgg", "ABORT 2")
                        ppComp?.PP_Abort()
                        return ""
                    }catch (e: Exception){ e.printStackTrace() }
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            cancelCheckEvent = false
            try {
                ppComp?.PP_StartRemoveCard("RETIRE O CARTAO")
                ppComp?.PP_RemoveCard()
                Log.i("msgg", "ABORT 1")
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
        try{
            ppComp?.PP_SetKbd(b1,b2,b3,b4,b5,b6,b7,b8,b9,b0,bCancel, bConfirm, bClear, activity)
        }catch (e: Exception){
            Log.i("msgg", "setKBD $e")
            e.printStackTrace()
        }

    }
//    fun getTagsString(input: String): String {
//        return String.format("%03d", tags.length / 2) + tags
//    }
//
//    fun getTagsOpString(input: String): String {
//        return String.format("%03d", tagsOpt.length / 2) + tagsOpt
//    }

    fun showKBD(activity: PinKbdActivity, mainActivity: MainActivity, requireContext: Context) {
        Log.d("msgg", "show kbd...")
        //openPinKBD(mainActivity, requireContext)
       // waitActivityOpen(activity)
        setKbd()
    }

    fun setKbd(){
        val kbdData = PinKbdActivity.mKBDData
        Log.d("msgg","setkbd WIP : bt1 ${kbdData!!.btn1}")
        ppComp?.PP_SetKbd(
            kbdData.btn1,
            kbdData.btn2,
            kbdData.btn3,
            kbdData.btn4,
            kbdData.btn5,
            kbdData.btn6,
            kbdData.btn7,
            kbdData.btn8,
            kbdData.btn9,
            kbdData.btn0,
            kbdData.btnCancel,
            kbdData.btnConfirm,
            kbdData.btnClear,
            kbdData.activity
        )
    }


    fun openPinKBD(activity: MainActivity, requireContext: Context) {
        val intent = Intent(requireContext, PinKbdActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
//            activity.startActivityForResult(intent,0)
            requireContext.startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun waitActivityOpen(pinActivity: PinKbdActivity) {
        Log.d("msgg", "wait activity PIN start")
        try {
            while (!PinKbdActivity.active) {
                Log.d("msgg", "waiting activity PIN")
            }
            Log.d("msgg", "waiting activity PIN")
//            Thread.sleep(1500)
//            PinKBDActivity.mKBDData.activity.runOnUiThread {
//                PinKBDActivity.getTextView().setText(textManta)
//            }
        } catch (e: java.lang.Exception) {
            Log.d("msgg", "wait activity PIN error")
            e.printStackTrace()
        }
    }

    fun removeCard(input: String){
        try{
            ppComp?.PP_StartRemoveCard(input)
            ppComp?.PP_RemoveCard()
        } catch(e: Exception){
            e.printStackTrace()
        }

    }

}