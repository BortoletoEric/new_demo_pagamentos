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
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentSucessPayMiniBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutCardInfoBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutPaymentInfoBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutPaymentInfoMiniBinding
import br.com.gertec.gedi.GEDI
import br.com.gertec.gedi.enums.GEDI_INFO_e_ControlNumber
import br.com.gertec.gedi.exceptions.GediException
import br.com.gertec.gedi.interfaces.IGEDI
import kotlin.random.Random

class SucessPayMiniFragment : Fragment() {

    private var _binding: FragmentSucessPayMiniBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardInfoBinding: LayoutCardInfoBinding
    private lateinit var paymentInfoBinding: LayoutPaymentInfoMiniBinding
    private lateinit var mainActivity: MainActivity

    private lateinit var amount: String
    private lateinit var pan: String
    private lateinit var applicationType: String
    private lateinit var codSale: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("msgg","SucessFrag In")
        _binding = FragmentSucessPayMiniBinding.inflate(inflater, container, false)

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
        applicationType = mainActivity.mainViewModel.applicationType
        codSale = getRandomCodSale().toString()

        binding.displayPaymentInfo.txtAmount.setText(amount)
        binding.displayCardInfo.txtApplicationType.setText(applicationType)
        binding.displayCardInfo.txtCodeSaleValue.setText(codSale)
        binding.displayCardInfo.txtFinalCardNumbers.setText(pan)
        binding.btnFinish.setOnClickListener(View.OnClickListener {
            view.findNavController()
                .navigate(SucessPayMiniFragmentDirections.actionSucessPayMiniFragmentToAmountFragment())
        })
    }

    private fun getRandomCodSale(): Int {
        return Random.nextInt(99999)
    }
}