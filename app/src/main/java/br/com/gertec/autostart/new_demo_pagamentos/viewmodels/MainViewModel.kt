package br.com.gertec.autostart.new_demo_pagamentos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.gertec.autostart.new_demo_pagamentos.commands.PPCompCommands
import java.util.regex.Pattern

class MainViewModel: ViewModel() {
    val ppCompCommands: PPCompCommands = PPCompCommands.getInstance()

    private var _processOk = MutableLiveData<String>()
    val processOk: LiveData<String>
        get() = _processOk

    init {
        _processOk.postValue("AMOUNT")
    }

    fun processCompleted(step: String){
        _processOk.postValue(step)
    }


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