package br.com.gertec.autostart.new_demo_pagamentos.commands

import android.content.Context
import android.util.Log
import android.widget.Toast
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.easylayer.printer.BarcodeFormat
import br.com.gertec.easylayer.printer.BarcodeType
import br.com.gertec.easylayer.printer.Printer
import br.com.gertec.easylayer.printer.PrinterError

class PrinterCommands(private val context: Context, private val activity: MainActivity) {
    var printerSuccess = false
    fun printComprovante(html: String?, barcode: String?) {
        val printer = Printer.getInstance(context, object : Printer.Listener {
            override fun onPrinterError(printerError: PrinterError) {
                activity.showSnackBar(printerError.cause, false)
                activity.mainViewModel.processCompleted("PRINT_NOK")
            }

            override fun onPrinterSuccessful(i: Int) {
                Log.i("TAG", "onPrinterSuccessful: ")
            }
        })

        val barcodeFormat = BarcodeFormat(BarcodeType.QR_CODE, BarcodeFormat.Size.FULL_PAPER)
        if (html != null) {
            printer.printHtml(context, html)
            printer.printBarcode(barcodeFormat, barcode.toString())
            try {
                printer.scrollPaper(130)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}