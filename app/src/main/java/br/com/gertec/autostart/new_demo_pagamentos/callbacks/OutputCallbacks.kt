package br.com.gertec.autostart.new_demo_pagamentos.callbacks

import android.content.Intent
import android.media.ToneGenerator
import android.util.Log
import android.view.View
import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.PinKbdActivity
import br.com.gertec.gedi.enums.GEDI_LED_e_Id
import br.com.gertec.gedi.exceptions.GediException
import br.com.gertec.gpos780.ppcomp.IPPCompDSPCallbacks
import java.util.concurrent.atomic.AtomicLong

class OutputCallbacks(var mainActivity: MainActivity) :
    IPPCompDSPCallbacks {

    private var mMenuTitle = ""
    private var mSelectedItem = 0
    private var sTxt2Pin = -1
    private var wasCancelBefore = false
    private var txtPinDisplay = ""
    private var stopKBD = false
    private var turnOnLed = true
    private var kbdStarted = false

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
            256L, 0L -> {
                Unit
            }//TAP_INSERT_SWIPE_CARD
            721153L -> {
                Unit
            } //TEXT_S
            64L -> { // DESLIGA TODOS OS LEDS
                Log.d("leds", "OutputCallBacks 64L: desliga todos")
                mainActivity.runOnUiThread{
                    try {
                        setAllLedsOff()
                    }
                    catch (e: GediException){
                        e.printStackTrace()
                        Log.e("leds", e.message.toString())
                        setEmulatedLedsVisibilityAllGone()
                    }
                }
            }
            72L -> { // LIGA LED AZUL - AGUARDANDO
                Log.d("leds", "OutputCallBacks 72L: azul aguardando")
                mainActivity.runOnUiThread{
                    try {
                        setAllLedsOff()
                        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_BLUE, true)
                    } catch (e: GediException) {
                        e.printStackTrace()
                        Log.e("leds", e.message.toString())
                        Log.e("leds", e.cause.toString())
                        Log.d("leds", "terminal não tem led azul físico, portanto é emulado")
                        setEmulatedLedsVisibilityAllGone()
                        mainActivity.binding.imageViewLedAzul.visibility = View.VISIBLE
                    }
                }
            }
            40L -> { //APARECE ENQUANTO AGUARDA CARTÃO
                Log.d("leds", "OutputCallBacks 40L: azul aguardando")
                mainActivity.runOnUiThread{
                    try {
                        setAllLedsOff()
                        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_BLUE, true)
                    } catch (e: GediException) {
                        e.printStackTrace()
                        Log.e("leds", e.message.toString())
                        Log.d("leds", "terminal não tem led azul físico, portanto é emulado")
                        setEmulatedLedsVisibilityAllGone()
                        mainActivity.binding.imageViewLedAzul.visibility = View.VISIBLE
                    }
                }
            }
            48L -> { //APARECE ENQUANTO AGUARDA CARTÃO
                Log.d("leds", "OutputCallBacks 48L: apaga todos e liga azul")
                mainActivity.runOnUiThread{
                    try {
                        setAllLedsOff()
                        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_BLUE, true)
                    } catch (e: GediException) {
                        e.printStackTrace()
                        Log.e("leds", e.message.toString())
                        Log.d("leds", "terminal não tem led azul físico, portanto é emulado")
                        setEmulatedLedsVisibilityAllGone()
                        mainActivity.binding.imageViewLedAzul.visibility = View.VISIBLE
                    }
                }
            }
            31003L -> { //APROXIME, INSIRA OU PASSE CARTÃO
                Log.d("leds", "OutputCallBacks 31003L: azul aguardando")
                mainActivity.runOnUiThread{
                    try {setAllLedsOff()
                        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_BLUE, true)
                    } catch (e: GediException) {
                        e.printStackTrace()
                        Log.e("leds", e.message.toString())
                        Log.d("leds", "terminal não tem led azul físico, portanto é emulado")
                        setEmulatedLedsVisibilityAllGone()
                        mainActivity.binding.imageViewLedAzul.visibility = View.VISIBLE
                    }
                }
            }
            68L -> { //LIGA LED LARANJA - LENDO
                mainActivity.runOnUiThread{
                    Log.d("leds", "OutputCallBacks 68L: laranja lendo")
                    try {
                        setAllLedsOff()
                        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_ORANGE, true)
                    } catch (e: GediException) {
                        e.printStackTrace()
                        Log.e("leds", e.message.toString())
                        Log.d("leds", "terminal não tem led laranja físico, portanto é emulado")
                        setEmulatedLedsVisibilityAllGone()
                        mainActivity.binding.imageViewLedLaranja.visibility = View.VISIBLE
                    }
                }
            }
            66L -> { //LIGA LED VERDE - OK
                mainActivity.runOnUiThread{
                    Log.d("leds", "OutputCallBacks 66L: verde sucesso")
                    try {
                        setAllLedsOff()
                        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_GREEN, true)
                    } catch (e: GediException) {
                        e.printStackTrace()
                        Log.e("leds", e.message.toString())
                        Log.d("leds", "terminal não tem led verde físico, portanto é emulado")
                        setEmulatedLedsVisibilityAllGone()
                        mainActivity.binding.imageViewLedVerde.visibility = View.VISIBLE
                    }
                }
            }
            65L -> { //LIGA LED VERMELHO - GRANDE ERRO
                mainActivity.runOnUiThread {
                    Log.d("leds", "OutputCallBacks 65L: vermelho erro")
                    try {
                        setAllLedsOff()
                        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_RED, true)
                        Thread.sleep(2000)
                        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_RED, false)
                    } catch (e: GediException) {
                        e.printStackTrace()
                        Log.e("leds", e.message.toString())
                        Log.d("leds", "terminal não tem led vermelho físico, portanto é emulado")
                        setEmulatedLedsVisibilityAllGone()
                        mainActivity.binding.imageViewLedVermelho.visibility = View.VISIBLE
                        Thread.sleep(2000)
                        mainActivity.binding.imageViewLedVermelho.visibility = View.GONE
                    }
                }
            }
            44L -> { //APARECE ENQUANTO AGUARDA CARTÃO
                mainActivity.runOnUiThread{
                    try {
                        setAllLedsOff()
                        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_BLUE, true)
                    } catch (e: GediException) {
                        e.printStackTrace()
                        Log.e("leds", e.message.toString())
                        Log.d("leds", "terminal não tem led azul físico, portanto é emulado")
                        setEmulatedLedsVisibilityAllGone()
                        mainActivity.binding.imageViewLedAzul.visibility = View.VISIBLE
                    }
                }
            }
            41L -> { //APARECE ENQUANTO AGUARDA CARTÃO
                mainActivity.runOnUiThread{
                    try {
                        setAllLedsOff()
                        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_BLUE, true)
                    } catch (e: GediException) {
                        e.printStackTrace()
                        Log.e("leds", e.message.toString())
                        Log.d("leds", "terminal não tem led azul físico, portanto é emulado")
                        setEmulatedLedsVisibilityAllGone()
                        mainActivity.binding.imageViewLedAzul.visibility = View.VISIBLE
                    }
                }
            }
            790657L -> {
                Unit
            } //UPDATING_TABLES
            790658L -> {
                Unit
            } //UPDATING_RECORD
            393220L -> {
                PinKbdActivity.mKBDData?.activity?.runOnUiThread(Runnable {
                    PinKbdActivity.mKBDData?.display?.text = sTxt1 + sTxt2
                    PinKbdActivity.mKBDData?.display?.text = sTxt1 + sTxt2
                })
            } //WRONG_PIN_S
            917504L -> {
                if (stopKBD) return
                txtPinDisplay = ""
                setupKBD()
                stopKBD = true
            } //PIN_STARTING if sTxt2.isEmpty(), else //PIN_STARTING_S
        }

        if(lFlags==512L){
            PinKbdActivity.mKBDData?.activity?.runOnUiThread(Runnable {
                PinKbdActivity.mKBDData?.amount?.text = mainActivity.mainViewModel.transactionAmount
                PinKbdActivity.mKBDData?.textView?.text = sTxt2
                })

            stopKBD = false

            if (BuildConfig.FLAVOR == "gpos760" ||
                BuildConfig.FLAVOR == "gpos700" ||
                BuildConfig.FLAVOR == "gpos780" ||
                BuildConfig.FLAVOR == "gpos700mini"
            ) beep()
        }
    }

    fun  setAllLedsOff() {
        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_BLUE, false)
        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_GREEN, false)
        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_ORANGE, false)
        mainActivity.mainViewModel.turnOnLed(GEDI_LED_e_Id.GEDI_LED_ID_RED, false)
    }

    fun setEmulatedLedsVisibilityAllGone() {
        mainActivity.binding.imageViewLedAzul.visibility = View.GONE
        mainActivity.binding.imageViewLedVerde.visibility = View.GONE
        mainActivity.binding.imageViewLedLaranja.visibility = View.GONE
        mainActivity.binding.imageViewLedVermelho.visibility = View.GONE
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
    private fun setupKBD() {
        //Inicia a tela do teclado
        openPinKBD()

        //Aguarda até que ela tenha sido iniciada
        waitActivityOpen()

        //Seta o teclado na PPComp
        Log.d("msgg","kbdStarted = $kbdStarted")
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
            while (!PinKbdActivity.active){
                Log.d("msgg","pinKbd active = ${PinKbdActivity.active}")
            }
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
