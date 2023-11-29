package br.com.gertec.autostart.new_demo_pagamentos.commands

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Button
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.ppcomp.IPPCompDSPCallbacks
import br.com.gertec.ppcomp.PPComp
import br.com.gertec.ppcomp.exceptions.PPCompDumbCardException
import br.com.gertec.ppcomp.exceptions.PPCompNoCardException
import br.com.gertec.ppcomp.exceptions.PPCompProcessingException
import br.com.gertec.ppcomp.exceptions.PPCompTabExpException
import br.com.gertec.ppcomp.exceptions.PPCompTimeoutException

class PPCompCommands private constructor() {
    private var ppComp: PPComp? = null
    private var cancelCheckEvent = false
    private val aidsList = listOf(
        "3141016107A000000003101000000000000000000001CREDITO         030084008300000769862010172648530001539918000918E0F0887000F0F00123D84000A8000010000000D84004F8000000C350R120000271000001388000013880001100000000000000000000000000000000000000000000000000000000000000000000000000000000Y1Z1Y3Z3DC4000A8000010000000DC4004F800",
        "3141010507A000000003101000000000000000000001CREDITO         030084008300000769862010172648530001539918000918E0F0D87000F0F00122D84000A8000010000000D84004F8000000C350R120000753000001388000013880001100000000000000000000000000000000000000000000000000000000000000000000000000000000Y1Z1Y3Z3DC4000A8000010000000DC4004F800",
        "3141010607A000000003201000000000000000000002ELECTRON        030084008300000769862010172648530001539918000918E0E0C07000F0F00122D84000A8000810000000D84004F80000000000R120000271000001388000013880001100000000000000000000000000000000000000000000000000000000000000000000000000000000Y1Z1Y3Z3DC4000A8000010000000DC4004F800",
        "3141010707A000000003301000000000000000000002ELECTRON2       030084008300000769862010172648530001539918000918E0E0C07000F0F00122D84000A8000810000000D84004F80000000000R120000271000001388000013880001100000000000000000000000000000000000000000000000000000000000000000000000000000000Y1Z1Y3Z3DC4000A8000010000000DC4004F800",
        "3141010807A000000004101000000000000000000001MASTERCARD CREDI030002000200000769862010172648530001539918000918E0F0E8F000F0A00122FC50ACA0000000000000FC50ACF80000000000R140000271000001388000013880001100000000000000000000000000000000000000000000000000000000000000000000000000000000Y1Z1Y3Z3FC508C88000000000000FC508C8800",
        "3141010907A000000004306000000000000000000002MAESTRO         030002000000000769862010172648530001539918000918E0F0E8F000F0F00122FC50BCA0000000000000FC50BCF80000000000R140000271000001388000013880001100000000000000000000000000000000000000000000000000000000000000000000000000000000Y1Z1Y3Z3FC500C88000000800000FC500C8800"
    )
    private val capksList = listOf(
        "61120401A0000000250101103FFFF096AFAD7010F884E2824650F764D47D7951A16EED6DBB881F384DEDB6702E0FB55C0FBEF945A2017705E5286FA249A591E194BDCD74B21720B44CE986F144237A25F95789F38B47EA957F9ADB2372F6D5D41340A147EAC2AF324E8358AE1120EF3FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "61120102A0000000250201103FFFF112AF4B8D230FDFCB1538E975795A1DB40C396A5359FAA31AE095CB522A5C82E7FFFB252860EC2833EC3D4A665F133DD934EE1148D81E2B7E03F92995DDF7EB7C90A75AB98E69C92EC91A533B21E1C4918B43AFED5780DE13A32BBD37EBC384FA3DD1A453E327C56024DACAEA74AA052C4DFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "61120103A0000000250301103FFFF128B0C2C6E2A6386933CD17C239496BF48C57E389164F2A96BFF133439AE8A77B20498BD4DC6959AB0C2D05D0723AF3668901937B674E5A2FA92DDD5E78EA9D75D79620173CC269B35F463B3D4AAFF2794F92E6C7A3FB95325D8AB95960C3066BE548087BCB6CE12688144A8B4A66228AE4659C634C99E36011584C095082A3A3E3FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "61120104A0000000250401103FFFF096D0F543F03F2517133EF2BA4A1104486758630DCFE3A883C77B4E4844E39A9BD6360D23E6644E1E071F196DDF2E4A68B4A3D93D14268D7240F6A14F0D714C17827D279D192E88931AF7300727AE9DA80A3F0E366AEBA61778171737989E1EE309FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "61120105A0000000250E01103FFFF144AA94A8C6DAD24F9BA56A27C09B01020819568B81A026BE9FD0A3416CA9A71166ED5084ED91CED47DD457DB7E6CBCD53E560BC5DF48ABC380993B6D549F5196CFA77DFB20A0296188E969A2772E8C4141665F8BB2516BA2C7B5FC91F8DA04E8D512EB0F6411516FB86FC021CE7E969DA94D33937909A53A57F907C40C22009DA7532CB3BE509AE173B39AD6A01BA5BB85FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "61120106A0000000256201103FFFF096BA29DE83090D8D5F4DFFCEB98918995A768F41D0183E1ACA3EF8D5ED9062853E4080E0D289A5CEDD4DD96B1FEA2C53428436CE15A2A1BFE69D46197D3F5A79BCF8F4858BFFA04EDB07FC5BE8560D9CE38F5C3CA3C742EDFDBAE3B5E6DDA45557FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000000000000000000000000000000000000000000000000000000000000000000000000000000"
    )


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
        } catch (e: Exception){
            return ""
            e.printStackTrace()
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
                aidsList.forEach{ aid ->
                    ppComp?.PP_TableLoadRec("01$aid")
                }
                capksList.forEach{ capk ->
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