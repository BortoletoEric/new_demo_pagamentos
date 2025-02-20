package br.com.gertec.autostart.new_demo_pagamentos.devices.wrapper

import android.content.Context
import br.com.gertec.gpos780.ppcomp.IPPCompDSPCallbacks

interface PPCompWrapper {
    fun init(context: Context)
    fun setDspCallback(outputCallbacks: IPPCompDSPCallbacks)
    fun open()
    fun abort()
    fun setKbd()
    fun selectLanguage(deviceLanguage: String)
    fun checkEvent(input: String): String?
    fun getCard(input: String, requireContext: Context): Pair<Boolean, String?>
    fun goOnChip(
        input: String,
        tags: String?,
        opTags: String?,
        requireContext: Context
    ): Pair<String?, String?>?
    fun finishChip()
    //TODO
}
