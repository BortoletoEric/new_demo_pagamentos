package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.commands.PrinterCommands
import br.com.gertec.autostart.new_demo_pagamentos.commands.Utils
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentSucessPayBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutCardInfoBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutPaymentInfoBinding
import br.com.gertec.gedi.GEDI
import br.com.gertec.gedi.enums.GEDI_INFO_e_ControlNumber
import br.com.gertec.gedi.exceptions.GediException
import br.com.gertec.gedi.interfaces.IGEDI
import java.util.Locale
import kotlin.random.Random

class SucessPayFragment : Fragment() {

    private var _binding: FragmentSucessPayBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardInfoBinding: LayoutCardInfoBinding
    private lateinit var paymentInfoBinding: LayoutPaymentInfoBinding
    private lateinit var mainActivity: MainActivity
    private val args: SucessPayFragmentArgs by navArgs()

    private lateinit var amount: String
    private lateinit var pan: String
    private lateinit var applicationType: String
    private lateinit var codSale: String

    private var numeroDeSerie: String = ""
    private var isPrintedLojista: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("msgg","SucessFrag In")
        _binding = FragmentSucessPayBinding.inflate(inflater, container, false)

        cardInfoBinding = binding.displayCardInfo
        paymentInfoBinding = binding.displayPaymentInfo
        mainActivity = (activity as MainActivity)
        mainActivity.mainViewModel.postNs("")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amount = mainActivity.mainViewModel.transactionAmount
        pan = mainActivity.mainViewModel.pan
        mainActivity.mainViewModel.pan = ""
        applicationType = mainActivity.mainViewModel.applicationType
        codSale = getRandomCodSale().toString()

        binding.btnPrint.requestFocus()
        binding.displayPaymentInfo.txtAmount.setText(amount)
        binding.displayCardInfo.txtApplicationType.setText(applicationType)
        binding.displayCardInfo.txtCodeSaleValue.setText(codSale)
        binding.displayCardInfo.txtFinalCardNumbers.setText(pan)
        binding.btnPrint.setOnClickListener(View.OnClickListener { printComprovante(getString(R.string.cliente), numeroDeSerie) })
        binding.btnFinish.setOnClickListener(View.OnClickListener {
            //mainActivity.mainViewModel.ppCompCommands.removeCard("GERTEC APP DEMO")
            view.findNavController()
                .navigate(SucessPayFragmentDirections.actionSucessPayFragmentToAmountFragment())
        })

        getNS()
        setupObservers(view)
    }

    private fun setupObservers(view: View) {
        mainActivity.mainViewModel.processOk.observe(viewLifecycleOwner) { step ->
            when (step) {
                "PRINT_NOK" -> {
                    //mainActivity.showSnackBar("ERRO NA IMPRESSÃO")
//                    view.findNavController().navigate(
//                        SucessPayFragmentDirections.actionSucessPayFragmentToAmountFragment()
//                    )
                }
            }
        }
        mainActivity.mainViewModel.ns.observe(viewLifecycleOwner){
            Thread.sleep(500)
            if(!it.isNullOrEmpty() && !isPrintedLojista){
                numeroDeSerie = it
                printComprovante(
                    getString(R.string.lojista),
                    it
                )
                isPrintedLojista = true
            }
        }
    }

    private fun printComprovante(user: String, ns: String) {
        val printerCommands = context?.let { PrinterCommands(it, mainActivity) }
        val html = Utils.getPaymentReceiptHtmlModel(amount, applicationType, codSale, ns, pan, user, getDeviceLanguage())
        val qrCode = Utils.getPaymentReceiptQrCode(amount, applicationType, codSale, ns, getDeviceLanguage())
        printerCommands?.printComprovante(html, qrCode)
    }

    fun getDeviceLanguage(): String {
        val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requireContext().resources.configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            requireContext().resources.configuration.locale
        }
        return locale.language
    }

    private fun getRandomCodSale(): Int {
        return Random.nextInt(99999)
    }

    private fun getNS(){
        Log.d("msgg","WD 0 <-------------")

        GEDI.init(context)
        val mGedi: IGEDI = GEDI.getInstance()
//        val mGedi: IGEDI = GEDI.getInstance(requireContext())

        Log.d("msgg","WD 1 <-------------")
        try {

            Log.d("msgg","WD 2 <-------------")
//            numeroDeSerie = mGedi.info.ControlNumberGet(GEDI_INFO_e_ControlNumber.SN)
            numeroDeSerie = mainActivity.mainViewModel.getNs()?:""
            Log.d("msgg","NS ESPERADO = $numeroDeSerie <-------------")
            mainActivity.mainViewModel.postNs(numeroDeSerie)
        } catch (e: GediException) {
            Log.e("GediException", "Erro ao obter o número de série: ", e)

            Log.d("msgg","WD 3 <-------------")
            mainActivity.mainViewModel.postNs(null)
        }
    }
}