package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.commands.PrinterCommands
import br.com.gertec.autostart.new_demo_pagamentos.commands.Utils
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentSucessPayBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutCardInfoBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutPaymentInfoBinding
import br.com.gertec.gedi.exceptions.GediException
import java.util.Locale
import kotlin.random.Random

class SucessPayFragment : Fragment() {
    private var _binding: FragmentSucessPayBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardInfoBinding: LayoutCardInfoBinding
    private lateinit var paymentInfoBinding: LayoutPaymentInfoBinding
    private lateinit var mainActivity: MainActivity
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

        setupViewsAndButtons(view)
        setupObservers()
        getNS()
    }

    private fun setupViewsAndButtons(view: View) {
        binding.btnPrint.requestFocus()
        binding.displayPaymentInfo.txtAmount.text = amount
        binding.displayCardInfo.txtApplicationType.text = applicationType
        binding.displayCardInfo.txtCodeSaleValue.text = codSale
        binding.displayCardInfo.txtFinalCardNumbers.text = pan
        binding.btnPrint.setOnClickListener {
            printComprovante(
                getString(R.string.cliente),
                numeroDeSerie
            )
        }
        binding.btnFinish.setOnClickListener {
            view.findNavController()
                .navigate(SucessPayFragmentDirections.actionSucessPayFragmentToAmountFragment())
        }
    }

    private fun setupObservers() {
        mainActivity.mainViewModel.ns.observe(viewLifecycleOwner) {
            Thread.sleep(500)
            if (!it.isNullOrEmpty() && !isPrintedLojista) {
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
        val html = Utils.getPaymentReceiptHtmlModel(
            amount,
            applicationType,
            codSale,
            ns,
            pan,
            user,
            getDeviceLanguage(),
            mainActivity.mainViewModel.timeBrAndUs
        )
        val qrCode =
            Utils.getPaymentReceiptQrCode(amount, applicationType, codSale, ns, getDeviceLanguage())
        printerCommands?.printComprovante(html, qrCode)
    }

    private fun getDeviceLanguage(): String {
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

    private fun getNS() {
        try {
            numeroDeSerie = mainActivity.mainViewModel.getNs() ?: ""
            mainActivity.mainViewModel.postNs(numeroDeSerie)
        } catch (e: GediException) {
            mainActivity.mainViewModel.postNs(null)
        }
    }
}