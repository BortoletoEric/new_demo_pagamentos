package br.com.gertec.autostart.new_demo_pagamentos.commands

import android.content.Context
import android.widget.Toast
import br.com.gertec.ppcomp.PPComp

class PPCompCommands private constructor() {
    private var ppComp: PPComp? = null
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

    fun close(message: String){
        ppComp?.PP_Close(message)
    }

    fun getCard(input: String): String?{
        ppComp?.PP_StartGetCard(input)
        return ppComp?.PP_GetCard()
    }

//    fun goOnChip(input: String, tags: String? = null, opTags: String? = null){
//        if(tags.isNullOrEmpty() || opTags.isNullOrEmpty()){
//            ppComp?.PP_StartGoOnChip(input)
//        }else{
//            ppComp?.PP_StartGoOnChip(input, getTagsString(input), getTagsOpString(input))
//        }
//        ppComp?.PP_GoOnChip()
//    }

//    fun getTagsString(input: String): String {
//        return String.format("%03d", tags.length / 2) + tags
//    }
//
//    fun getTagsOpString(input: String): String {
//        return String.format("%03d", tagsOpt.length / 2) + tagsOpt
//    }

    fun removeCard(input: String){
        ppComp?.PP_StartRemoveCard(input)
        ppComp?.PP_RemoveCard()
    }

}