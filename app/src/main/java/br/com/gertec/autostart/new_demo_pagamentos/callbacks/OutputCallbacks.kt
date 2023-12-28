package br.com.gertec.autostart.new_demo_pagamentos.callbacks

import android.content.Intent
import android.media.ToneGenerator
import android.util.Log
import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.PinKbdActivity
import br.com.gertec.ppcomp.IPPCompDSPCallbacks
import java.util.concurrent.atomic.AtomicLong

class OutputCallbacks(private var mainActivity: MainActivity) :
    IPPCompDSPCallbacks {

    private var mMenuTitle = ""
    private var mSelectedItem = 0
    private var sTxt2Pin = -1
    private var wasCancelBefore = false
    private var txtPinDisplay = ""
    private var stopKBD = false

    override fun Text(lFlags: Long, sTxt1: String, sTxt2: String) {
        mainActivity.mainViewModel.updateDisplay(lFlags, sTxt1, sTxt2)

        Log.d("msgg","OUTPUT CALLBACK ($lFlags): $sTxt1 \n $sTxt2")

        when (lFlags) {
            135170L -> {
                Unit
            } //INSERT_SWIPE_CARD
            327940L -> {
                Unit
            } //INVALID_APP
            4099L -> {
                Unit
            } //SECOND_TAP
            524292L -> {
                Unit
            } //PIN_BLOCKED
            589824L -> {
                Unit
            } //PIN_LAST_TRY
            69632L -> {
                Unit
            } //PROCESSING
            724993L -> {
                stopKBD = false
            } //REMOVE CARD
            851968L -> {
                Unit
            } //SELECTED_S
            200707L -> {
                Unit
            } //TAP_INSERT_SWIPE_CARD
            256L, 0L, 721153L -> {
                Unit
            } //TEXT_S
            790657L -> {
                Unit
            } //UPDATING_TABLES
            790658L -> {
                Unit
            } //UPDATING_RECORD
            393220L -> {
                PinKbdActivity.mKBDData?.activity?.runOnUiThread(Runnable {
                    PinKbdActivity.mKBDData?.display?.text = sTxt1 + sTxt2
                })
            } //WRONG_PIN_S
            917504L -> {
                if (stopKBD) return
                txtPinDisplay = ""
                showKBD()
                stopKBD = true
            } //PIN_STARTING if sTxt2.isEmpty(), else //PIN_STARTING_S
            512L -> {
                PinKbdActivity.mKBDData.let {
                    it?.activity?.runOnUiThread(Runnable {
                        it.amount?.text = mainActivity.mainViewModel.transactionAmount
                        it.textView?.text = sTxt2
                    })
                }
                stopKBD = false

                if (BuildConfig.FLAVOR == "gpos760" ||
                    BuildConfig.FLAVOR == "gpos700" ||
                    BuildConfig.FLAVOR == "gpos780"
                ) beep()
            }
        }
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

    override fun MenuShow(lFlags: Long, lsOpts: List<String>, iOptSel: Int) = mSelectedItem

    //Mostra o teclado e PIN
    private fun showKBD() {
        //Inicia a tela do teclado
        openPinKBD()

        //Aguarda até que ela tenha sido iniciada
        waitActivityOpen()

        //Seta o teclado na PPComp
        mainActivity.mainViewModel.ppCompCommands.setKbd()
    }

    private fun openPinKBD() {
        val intent = Intent(mainActivity, PinKbdActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            mainActivity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun waitActivityOpen(): Int {
        try {
            while (!PinKbdActivity.active) Unit
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    private fun beep() {
        try {
            val toneGen = ToneGenerator(4, 100)
            toneGen.startTone(93, 200)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
