package br.com.gertec.autostart.new_demo_pagamentos.commands

import java.text.NumberFormat

class Utils {
    companion object {
        fun centsToCurrentString(cents: Double): String? {
            return NumberFormat.getCurrencyInstance().format(cents.toFloat() / 100)
        }

        fun getPaymentReceiptHtmlModel(
            amount: String,
            applicationType: String,
            codSale: String, ns: String?, pan: String,
            user: String
        ): String? {
            val head = "<!DOCTYPE html><html>" +
                    "<head> <meta charset='UTF-8'>                              " +
                    "<style type='text/css'>                                    " +
                    "  h2  { margin: 0 0 -12px 0; letter-spacing: -2px;}  " +
                    "  h4  { margin: 0;}  " +
                    "  right   { float:right; }     " +
                    "  left    { float:left; }      " +
                    "  p    { margin: 0; }                                      " +
                    "  b    { margin: 0; }                                      " +
                    "  hr { border-top: 2px solid black; }                      " +
                    "  body { font-size: 10px; font-family: sans serif;}        " +
                    "</style>                                                   " +
                    "</head>"
            val body = "<body>" +
                    "<b style=\"font-size:14px\"> Demonstração <right>VIA " + user + "</right></b>" +
                    "<hr></hr>" +
                    "<b> Gertec <right>CNPJ: 03.654.119/0001-76</right> </b>" +
                    "<hr></hr>" +
                    "<h2> TOTAL: <right> " + amount + " </right> </h2> <br>" +
                    "<h4> " + applicationType + " <right> " + codSale + " </right> </h4> <br>" +
                    "<h4> CARTÃO <right> ************* " + pan + "</right> </h4>" +
                    "<hr></hr>" +
                    "<b>25/09/20 - 11h09 <right> (C) </right> </b>" +
                    "<b>Auto: 73664829 <right> Term: " + ns + "</right></b> </br>" +
                    "</body>" +
                    "</html>"
            return head + body
        }

        fun getPaymentReceiptQrCode(
            amount: String,
            applicationType: String,
            codSale: String,
            ns: String?
        ): String? {
            return ("Comprovante de pagamento" +
                    "\nDemonstração - Gertec" +
                    "\nCNPJ: 03.654.119/0001-76" +
                    "\nValor da compra: ${amount}" +
                    "\nPagamento no: ${applicationType}" +
                    "\nCódigo da venda: ${codSale}" +
                    "\nTerminal: ${ns}")
        }
    }

}