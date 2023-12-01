package br.com.gertec.autostart.new_demo_pagamentos.acitivities

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.gertec.autostart.new_demo_pagamentos.databinding.ActivityPinKbdBinding

class PinKbdActivity : AppCompatActivity() {
    var active = false
    lateinit var mKBDData: KbdData
    private var _binding: ActivityPinKbdBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPinKbdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rootView = window.decorView.rootView

        mKBDData = KbdData()
        mKBDData.btn0 = binding.btn0Tag
        mKBDData.btn1 = binding.btn1Tag
        mKBDData.btn2 = binding.btn2Tag
        mKBDData.btn3 = binding.btn3Tag
        mKBDData.btn4 = binding.btn4Tag
        mKBDData.btn5 = binding.btn5Tag
        mKBDData.btn6 = binding.btn6Tag
        mKBDData.btn7 = binding.btn7Tag
        mKBDData.btn8 = binding.btn8Tag
        mKBDData.btn9 = binding.btn9Tag
        mKBDData.btnCancel = binding.btnCancelTag
        mKBDData.btnClear = binding.btnClearTag
        mKBDData.btnConfirm = binding.btnEnterTag
        //mKBDData?.textView = rootView.findViewWithTag(getString(R.string.lblDigitsTag));
        mKBDData.activity = this;
    }


    override fun onStart() {
        super.onStart()
        active = true
    }

    override fun onStop() {
        super.onStop()
        active = false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    class KbdData {
        var btn0: Button? = null
        var btn1: Button? = null
        var btn2: Button? = null
        var btn3: Button? = null
        var btn4: Button? = null
        var btn5: Button? = null
        var btn6: Button? = null
        var btn7: Button? = null
        var btn8: Button? = null
        var btn9: Button? = null
        var btnCancel: Button? = null
        var btnClear: Button? = null
        var btnConfirm: Button? = null
        var textView: TextView? = null
        var activity: Activity? = null
    }
}

