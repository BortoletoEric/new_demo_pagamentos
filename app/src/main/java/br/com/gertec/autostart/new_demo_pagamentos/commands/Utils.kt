package br.com.gertec.autostart.new_demo_pagamentos.commands

import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig


class Utils {
//    companion object {
//        fun getPaymentReceiptHtmlModel(
//            amount: String,
//            applicationType: String,
//            codSale: String, ns: String?, pan: String,
//            user: String, language: String, timeNDate: Pair<String, String>
//        ): String {
//            val head = "<!DOCTYPE html><html>" +
//                    "<head> <meta charset='UTF-8'>                              " +
//                    "<style type='text/css'>                                    " +
//                    "  h2  { margin: 0 0 -12px 0; letter-spacing: -2px;}  " +
//                    "  h4  { margin: 0;}  " +
//                    "  right   { float:right; }     " +
//                    "  left    { float:left; }      " +
//                    "  p    { margin: 0; }                                      " +
//                    "  b    { margin: 0; }                                      " +
//                    "  hr { border-top: 2px solid black; }                      " +
//                    "  body { font-size: 10px; font-family: sans serif;}        " +
//                    "</style>                                                   " +
//                    "</head>"
//            val body = "<body>" +
//            "<b style=\"font-size:14px\"> Demonstração <right>VIA " + user + " </right></b>" +
//                    "<hr></hr>" +
//                    "<b> Gertec <right>CNPJ: 03.654.119/0001-76</right> </b>" +
//                    "<hr></hr>" +
//                    "<h2> TOTAL: <right> " + amount + " </right> </h2> <br>" +
//                    "<h4> " + applicationType + " <right> " + codSale + " </right> </h4> <br>" +
//                    "<h4> CARTÃO <right> ************* " + pan + "</right> </h4>" +
//                    "<hr></hr>" +
//                    "<b>" + timeNDate.first + " <right> (C) </right> </b>" +
//                    "<b>Auto: 73664829 <right> Term: " + ns + "</right></b> </br>" +
//                    if (BuildConfig.FLAVOR.equals("gpos760") || BuildConfig.FLAVOR.contains("gpos700")) {
//                        "<br><br><br><br><br><br><br><br><br><br>"
//                    } else if (BuildConfig.FLAVOR.contains("gpos790")) {
//                        ""
//                    } else {
//                        "<br><br><br><br>"
//                    }
//                    "</body>" +
//                    "</html>"
//            val bodyEnglish = "<body>" +
//                    "<b style=\"font-size:14px\"> Demonstration <right>" + user + "'S COPY</right></b>" +
//                    "<hr></hr>" +
//                    "<b> Gertec <right>CNPJ: 03.654.119/0001-76</right> </b>" +
//                    "<hr></hr>" +
//                    "<h2> AMOUNT: <right> " + amount + " </right> </h2> <br>" +
//                    "<h4> " + applicationType + " <right> " + codSale + " </right> </h4> <br>" +
//                    "<h4> CARD <right> ************* " + pan + "</right> </h4>" +
//                    "<hr></hr>" +
//                    "<b>" + timeNDate.second + " <right> (C) </right> </b>" +
//                    "<b>Auto: 73664829 <right> Term: " + ns + "</right></b> </br>" +
//                    if (BuildConfig.FLAVOR.equals("gpos760")) {
//                        "<br><br><br><br><br><br><br><br><br><br>"
//                    } else {
//                        "<br><br><br><br><br>"
//                    }
//                    "</body>" +
//                    "</html>"
//            return head + if (language == "pt") body else bodyEnglish
//        }
//    }

    companion object {
        fun getPaymentReceiptHtmlModel(
            amount: String,
            applicationType: String,
            codSale: String, ns: String?, pan: String,
            user: String, language: String, timeNDate: Pair<String, String>
        ): String {
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
                    "<b style=\"font-size:14px\"> Demonstração <right>VIA " + user + " </right></b>" +
                    "<hr></hr>" +
                    "<b> Gertec <right>CNPJ: 03.654.119/0001-76</right> </b>" +
                    "<hr></hr>" +
                    "<h2> TOTAL: <right> " + amount + " </right> </h2> <br>" +
                    "<h4> " + applicationType + " <right> " + codSale + " </right> </h4> <br>" +
                    "<h4> CARTÃO <right> ************* " + pan + "</right> </h4>" +
                    "<hr></hr>" +
                    "<b>" + timeNDate.first + " <right> (C) </right> </b>" +
                    "<b>Auto: 73664829 <right> Term: " + ns + "</right></b> </br>"


            "</body>" +
                    "</html>"
            val bodyEnglish = "<body>" +
                    "<b style=\"font-size:14px\"> Demonstration <right>" + user + "'S COPY</right></b>" +
                    "<hr></hr>" +
                    "<b> Gertec <right>CNPJ: 03.654.119/0001-76</right> </b>" +
                    "<hr></hr>" +
                    "<h2> AMOUNT: <right> " + amount + " </right> </h2> <br>" +
                    "<h4> " + applicationType + " <right> " + codSale + " </right> </h4> <br>" +
                    "<h4> CARD <right> ************* " + pan + "</right> </h4>" +
                    "<hr></hr>" +
                    "<b>" + timeNDate.second + " <right> (C) </right> </b>" +
                    "<b>Auto: 73664829 <right> Term: " + ns + "</right></b> </br>" +
                    if (BuildConfig.FLAVOR.equals("gpos760")) {
                        "<br><br><br><br><br><br><br><br><br><br>"
                    } else {
                        "<br><br><br><br><br>"
                    }
            "</body>" +
                    "</html>"
            return head + if (language == "pt") body else bodyEnglish
        }
    }
}

