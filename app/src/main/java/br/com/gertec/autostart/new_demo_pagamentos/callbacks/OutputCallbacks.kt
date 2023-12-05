package br.com.gertec.autostart.new_demo_pagamentos.callbacks

import android.content.Intent
import android.media.ToneGenerator
import android.util.Log
import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.PinKbdActivity
import br.com.gertec.ppcomp.IPPCompDSPCallbacks
import java.util.concurrent.atomic.AtomicLong

class OutputCallbacks(var mainActivity: MainActivity) :
    IPPCompDSPCallbacks {
    var mMenuTitle = ""
    var mSelectedItem = 0
    var sTxt2Pin = -1
    var isClear = false
    var wasCancelBefore = false
    var TAG = "ExemploGCR_GOC_OutputCallbacks"
    var txtPinDisplay = ""
    override fun Text(lFlags: Long, sTxt1: String, sTxt2: String) {
        Log.d("msgg","($lFlags)OUT: $sTxt1\n$sTxt2")
        mainActivity.mainViewModel.updateDisplay(lFlags, sTxt1?:"", sTxt2?:"")
        if (lFlags == 135170L) {
            //INSERT_SWIPE_CARD
        } else if (lFlags == 327940L) {
            //INVALID_APP
        } else if (lFlags == 4099L) {
            //SECOND_TAP
        } else if (lFlags == 524292L) {
            //PIN_BLOCKED
        } else if (lFlags == 589824L) {
            //PIN_LAST_TRY
        } else if (lFlags == 69632L) {
            //PROCESSING
        } else if (lFlags == 724993L) {
            //REMOVE_CARD
        } else if (lFlags == 851968L) {
            val selected = sTxt1.split("\r".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            //SELECTED_S
        } else if (lFlags == 200707L) {
            //TAP_INSERT_SWIPE_CARD
        } else if (lFlags == 256L || lFlags == 0L || lFlags == 721153L) {
            //TEXT_S
        } else if (lFlags == 790657L) {
            //UPDATING_TABLES
        } else if (lFlags == 790658L) {
            //UPDATING_RECORD
        } else if (lFlags == 393220L) {
            //WRONG_PIN_S
            PinKbdActivity.mKBDData?.activity?.runOnUiThread(Runnable {
                PinKbdActivity.mKBDData?.display?.text = sTxt1 + sTxt2
            })
        } else if (lFlags == 917504L) {
            if (sTxt2.length == 0) {
                //PIN_STARTING
            } else {
                //PIN_STARTING_S
            }
            txtPinDisplay = ""
            Log.d(TAG, "SHOW KBD")
            Log.d("msgg", "flag 917504")
            showKBD()
        }
        if (lFlags == 512L) {
            PinKbdActivity.mKBDData?.activity?.runOnUiThread(Runnable {
                PinKbdActivity.mKBDData?.textView?.text = sTxt2
            })
        }
//        if (lFlags == 512L && sTxt2.length == 0 && sTxt2Pin == -1) {
//            isClear = false
//            sTxt2Pin = sTxt2.length
//        } else if (sTxt2Pin == sTxt2.length) {
//            mMenuTitle = ""
//        } else if (lFlags == 512L && sTxt2.length >= 0 && sTxt2Pin != -1) {
//            if (sTxt2Pin <= sTxt2.length) {
//                isClear = false
//                wasCancelBefore = false
//                //PIN Entry
//                beep()
//                txtPinDisplay = "$txtPinDisplay*"
//                PinKbdActivity.mKBDData?.activity?.runOnUiThread(Runnable {
//                    PinKbdActivity?.mKBDData?.textView?.text = txtPinDisplay
//                })
//            } else if (sTxt2Pin > sTxt2.length && !isClear) {
//                wasCancelBefore = false
//                beep()
//                isClear = true
//                txtPinDisplay = ""
//                PinKbdActivity.mKBDData?.activity?.runOnUiThread(Runnable {
//                    PinKbdActivity.mKBDData?.textView?.text = txtPinDisplay
//                })
//            }
//            sTxt2Pin = sTxt2.length
//        }
    }

    override fun Clear() {
        if (!wasCancelBefore && sTxt2Pin > 0) {
            wasCancelBefore = true
        }
    }

    override fun MenuStart(sTitle: String, lFlags: AtomicLong): Int {
        mMenuTitle = sTitle
        lFlags.set(IPPCompDSPCallbacks.DSP_F_BLOCK.toLong())
        //SELECT
        return 100
    }

    override fun MenuShow(lFlags: Long, lsOpts: List<String>, iOptSel: Int): Int {
        Log.d(TAG, "MenuShow() callback invoked.")
        Log.d(TAG, "lFlags=$lFlags")
        Log.d(TAG, "saOpts=")
        for (opt in lsOpts) {
            Log.d(TAG, "\t" + opt)
        }
        Log.d(TAG, "iOptSel=$iOptSel")
        return mSelectedItem
    }

    fun beep() {
        try {
            val toneGen = ToneGenerator(4, 100)
            toneGen.startTone(93, 200)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Mostra o teclado e PIN
    fun showKBD() {
        Log.d("msgg", "showKbd")
        //Inicia a tela do teclado
        openPinKBD()
        //Aguarda até que ela tenha sido iniciada
        waitActivityOpen()
//        val kbdData: KBDData? = PinKBDActivity.kBDData
        //Seta o teclado na PPComp
//        Log.d("msgg", "kbd ntn" + kbdData?.btn1)
        mainActivity.mainViewModel.ppCompCommands.setKbd()
//        ppComp.PP_SetKbd(
//            kbdData?.btn1,
//            kbdData?.btn2,
//            kbdData?.btn3,
//            kbdData?.btn4,
//            kbdData?.btn5,
//            kbdData?.btn6,
//            kbdData?.btn7,
//            kbdData?.btn8,
//            kbdData?.btn9,
//            kbdData?.btn0,
//            kbdData?.btnCancel,
//            kbdData?.btnConfirm,
//            kbdData?.btnClear,
//            kbdData?.activity
//        )
    }

    fun openPinKBD() {
        val intent = Intent(mainActivity, PinKbdActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            mainActivity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun waitActivityOpen(): Int {
        Log.d("msgg", "wait activity PIN start")
        try {
            while (!PinKbdActivity.active) {
                Log.d("msgg", "waiting activity PIN")
            }
            Log.d("msgg", "wait activity PIN end")
        } catch (e: Exception) {
            Log.d("msgg", "wait activity PIN error")
            e.printStackTrace()
        }
        return 0
    }
}
