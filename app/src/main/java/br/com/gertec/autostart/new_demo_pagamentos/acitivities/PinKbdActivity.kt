package br.com.gertec.autostart.new_demo_pagamentos.acitivities

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.model.KBDData

class PinKbdActivity : Activity() {
    companion object {
        private val TAG = PinKbdActivity::class.java.name
        var mKBDData: KBDData? = null
        var active = false
        val kBDData: KBDData?
            get() = mKBDData
    }

    /******************** lifecycle functions ****************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFinishOnTouchOutside(false)
        setContentView(R.layout.activity_manta)

        val rootView = window.decorView.rootView
        val frameLayout_pin = findViewById<FrameLayout>(R.id.frameMantaLayout_pin)
        var child: View? = null

        child = layoutInflater.inflate(R.layout.teclado_pin, null)
        frameLayout_pin.addView(child)

        setupKbdData(rootView)
//        enablePinKbd(rootView)
    }
//
//    private fun enablePinKbd(rootView: View) {
//        rootView.findViewWithTag<Button>(getString(R.string.btn0Tag))
//        rootView.findViewWithTag<Button>(getString(R.string.btn1Tag)).isEnabled = true
//        rootView.findViewWithTag<Button>(getString(R.string.btn2Tag)).isEnabled = true
//        rootView.findViewWithTag<Button>(getString(R.string.btn3Tag)).isEnabled = true
//        rootView.findViewWithTag<Button>(getString(R.string.btn4Tag)).isEnabled = true
//        rootView.findViewWithTag<Button>(getString(R.string.btn5Tag)).isEnabled = true
//        rootView.findViewWithTag<Button>(getString(R.string.btn6Tag)).isEnabled = true
//        rootView.findViewWithTag<Button>(getString(R.string.btn7Tag)).isEnabled = true
//        rootView.findViewWithTag<Button>(getString(R.string.btn8Tag)).isEnabled = true
//        rootView.findViewWithTag<Button>(getString(R.string.btn9Tag)).isEnabled = true
//        rootView.findViewWithTag<Button>(getString(R.string.btnCancelTag)).isEnabled = true
//        rootView.findViewWithTag<Button>(getString(R.string.btnClearTag)).isEnabled = true
//        rootView.findViewWithTag<Button>(getString(R.string.btnEnterTag)).isEnabled = true
//    }

    override fun onStart() {
        super.onStart()
        active = true
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onStop() {
        super.onStop()
        active = false
    }

    /******************** other functions ****************************/

    private fun setupKbdData(rootView: View) {
        mKBDData = KBDData()
        mKBDData?.btn0 = rootView.findViewWithTag(getString(R.string.btn0Tag))
        mKBDData?.btn1 = rootView.findViewWithTag(getString(R.string.btn1Tag))
        mKBDData?.btn2 = rootView.findViewWithTag(getString(R.string.btn2Tag))
        mKBDData?.btn3 = rootView.findViewWithTag(getString(R.string.btn3Tag))
        mKBDData?.btn4 = rootView.findViewWithTag(getString(R.string.btn4Tag))
        mKBDData?.btn5 = rootView.findViewWithTag(getString(R.string.btn5Tag))
        mKBDData?.btn6 = rootView.findViewWithTag(getString(R.string.btn6Tag))
        mKBDData?.btn7 = rootView.findViewWithTag(getString(R.string.btn7Tag))
        mKBDData?.btn8 = rootView.findViewWithTag(getString(R.string.btn8Tag))
        mKBDData?.btn9 = rootView.findViewWithTag(getString(R.string.btn9Tag))
        mKBDData?.btnCancel = rootView.findViewWithTag(getString(R.string.btnCancelTag))
        mKBDData?.btnClear = rootView.findViewWithTag(getString(R.string.btnClearTag))
        mKBDData?.btnConfirm = rootView.findViewWithTag(getString(R.string.btnEnterTag))
        mKBDData?.textView = rootView.findViewWithTag(getString(R.string.lblDigitsTag))
        mKBDData?.activity = this@PinKbdActivity
        mKBDData?.display = rootView.findViewWithTag(getString(R.string.display))
        mKBDData?.amount = rootView.findViewWithTag("amount")
    }

}