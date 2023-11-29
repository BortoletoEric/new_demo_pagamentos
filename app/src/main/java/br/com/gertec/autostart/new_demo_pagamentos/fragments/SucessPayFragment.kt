package br.com.gertec.autostart.new_demo_pagamentos.fragments

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

    private val VIA_CLIENTE: String = "CLIENTE"
    private val VIA_LOJISTA: String = "LOJISTA"
    private var numeroDeSerie: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSucessPayBinding.inflate(inflater, container, false)

        cardInfoBinding = binding.displayCardInfo
        paymentInfoBinding = binding.displayPaymentInfo
        mainActivity = (activity as MainActivity)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GEDI.init(context)
        setupObservers(view)

        amount = mainActivity.mainViewModel.transactionAmount
        pan = mainActivity.mainViewModel.pan
        applicationType = mainActivity.mainViewModel.applicationType
        codSale = getRandomCodSale().toString()


        binding.displayPaymentInfo.txtAmount.setText(amount)
        binding.displayCardInfo.txtApplicationType.setText(applicationType)
        binding.displayCardInfo.txtCodeSaleValue.setText(codSale)
        binding.btnPrint.setOnClickListener(View.OnClickListener { printComprovante(VIA_CLIENTE, numeroDeSerie) })
        binding.btnFinish.setOnClickListener(View.OnClickListener {
            //mainActivity.mainViewModel.ppCompCommands.removeCard("GERTEC APP DEMO")
            view.findNavController()
                .navigate(SucessPayFragmentDirections.actionSucessPayFragmentToAmountFragment())
        })

        getNS()
    }

    private fun setupObservers(view: View) {
        mainActivity.mainViewModel.processOk.observe(viewLifecycleOwner) { step ->
            when (step) {
                "PRINT_NOK" -> {
                    view.findNavController().navigate(
                        SucessPayFragmentDirections.actionSucessPayFragmentToAmountFragment()
                    )
                }
            }
        }
        mainActivity.mainViewModel.ns.observe(viewLifecycleOwner){
            numeroDeSerie = it
            printComprovante(VIA_LOJISTA, it)
        }
    }

    private fun printComprovante(user: String, ns: String) {
        val printerCommands = context?.let { PrinterCommands(it, mainActivity) }
        val html = Utils.getPaymentReceiptHtmlModel(amount, applicationType, codSale, ns, pan, user)
        val qrCode = Utils.getPaymentReceiptQrCode(amount, applicationType, codSale, ns)
        printerCommands?.printComprovante(html, qrCode)
    }

    private fun getRandomCodSale(): Int {
        return Random.nextInt(99999)
    }

    private fun getNS(){
        val mGedi: IGEDI = GEDI.getInstance(context)
        try {
            numeroDeSerie = mGedi.info.ControlNumberGet(GEDI_INFO_e_ControlNumber.SN)
            mainActivity.mainViewModel.postNs(numeroDeSerie)
        } catch (e: GediException) {
            Log.e("GediException", "Erro ao obter o número de série: ", e)
            mainActivity.mainViewModel.postNs(null)
        }
    }
}