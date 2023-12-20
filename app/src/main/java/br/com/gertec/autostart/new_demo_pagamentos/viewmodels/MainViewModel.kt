package br.com.gertec.autostart.new_demo_pagamentos.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig
import br.com.gertec.autostart.new_demo_pagamentos.commands.PPCompCommands
import br.com.gertec.gedi.GEDI
import br.com.gertec.gedi.enums.GEDI_INFO_e_ControlNumber
import br.com.gertec.gedi.interfaces.IGEDI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

class MainViewModel: ViewModel() {
    val ppCompCommands: PPCompCommands = PPCompCommands.getInstance()

    private var _processOk = MutableLiveData<String>()
    val processOk: LiveData<String>
        get() = _processOk

    private var _ns = MutableLiveData<String>()
    val ns: LiveData<String>
        get() = _ns

    private var _display = MutableLiveData<List<Any>>()
    val display: LiveData<List<Any>>
        get() = _display

    private var _kbdKeyPressed = MutableLiveData<Int>()
    val kbdKeyPressed: LiveData<Int>
        get() = _kbdKeyPressed

    //dados da transação
    var pan = ""
    var transactionAmount = ""
    var applicationType = ""
    var timeBrAndUs = Pair("","")
    private var iGedi: IGEDI? = null
    private var gediOk = false

    init {
        _processOk.postValue("AMOUNT")
        _display.postValue(listOf(0L,"",""))
        _kbdKeyPressed.postValue(-999)
    }

    fun processCompleted(step: String){
        _processOk.postValue(step)
    }
    fun updateDisplay(flag: Long, msg1: String, msg2: String){
        _display.postValue(listOf(flag,msg1, msg2))
    }

    fun keyPressed(keyCode: Int){
        _kbdKeyPressed.postValue(keyCode)
    }

    fun postNs(ns: String?){
        _ns.postValue(ns?:"")
    }

    fun setupGedi(ctxt: Context){
        viewModelScope.launch {
            if(!gediOk){
                while (true){
                    GEDI.init(ctxt)
                    iGedi = GEDI.getInstance()
                    delay(1000)
                    if(iGedi != null) break
                    Log.d("msgg","init gedi...")
                }
                Log.d("msgg","gediSetup = $iGedi")
                gediOk = true
            }
        }
    }

    fun beep() {
        iGedi?.audio?.Beep()
    }

    fun getNs(): String? {
        Log.d("msgg","gedi = $iGedi")
        return iGedi?.info?.ControlNumberGet(GEDI_INFO_e_ControlNumber.SN)
    }

    fun getDateAndTime(): String {
        val dateFormatBr = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Calendar.getInstance().time)
        val dateFormatUs = SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(Calendar.getInstance().time)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().time)
        timeBrAndUs = Pair("$dateFormatBr - $timeFormat","$dateFormatUs - $timeFormat")
        return SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Calendar.getInstance().time) + SimpleDateFormat("HHmmss", Locale.getDefault()).format(Calendar.getInstance().time)
    }

//    fun gediBeep(ctxt: Context){
//        GEDI.init(ctxt)
//        iGedi?.audio?.Beep()
//    }


//    fun ppCompInput(command: String, input: String){
//        when(command){
//            "GCR" -> {
//                val p = Pattern.compile("^(.{2})(.{2})(.{12})(.{12})(.{10})(.{2})(.*)$")
//                val m = p.matcher(input)
//
//                if (m.matches()) {
//                    this.networkId = m.group(1).toInt()
//                    this.applicationType = m.group(2).toInt()
//                    this.amount = m.group(3).toInt()
//                    val dateTimeStr = m.group(4)
//                    this.date = Utils.strToDateTimeFormat(dateTimeStr)
//                    this.timestamp = m.group(5)
//                    this.numberOfEntries = m.group(6).toInt()
//                    this.restInputTemp = m.group(7)
//                }
//            }
//            "GOC" -> {}
//            "FNC" -> {}
//        }
//
//    }


}

class MainViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}