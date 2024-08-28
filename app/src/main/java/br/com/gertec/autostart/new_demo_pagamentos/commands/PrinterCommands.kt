package br.com.gertec.autostart.new_demo_pagamentos.commands

import android.content.Context
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.easylayer.printer.BarcodeFormat
import br.com.gertec.easylayer.printer.BarcodeType
import br.com.gertec.easylayer.printer.Printer
import br.com.gertec.easylayer.printer.PrinterError

class PrinterCommands(private val context: Context, private val activity: MainActivity) {
    fun printComprovante(html: String?) {
        val printer = Printer.getInstance(context, object : Printer.Listener {
            override fun onPrinterError(printerError: PrinterError) {
                activity.showSnackBar(printerError.cause, false)
                activity.mainViewModel.processCompleted("PRINT_NOK")
            }

            override fun onPrinterSuccessful(i: Int) = Unit

        })

        printer.printHtml(context, html!!)
    }
}