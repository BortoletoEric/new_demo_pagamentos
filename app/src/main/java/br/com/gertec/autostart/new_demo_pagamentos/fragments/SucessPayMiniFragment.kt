package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentSucessPayMiniBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutCardInfoBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutPaymentInfoMiniBinding
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
        mainActivity.mainViewModel.pan = ""
        applicationType = mainActivity.mainViewModel.applicationType
        codSale = getRandomCodSale().toString()

        setupViewsAndButtons(view)
    }

    private fun setupViewsAndButtons(view: View) {
        binding.displayPaymentInfo.txtAmount.text = amount
        binding.displayCardInfo.txtApplicationType.text = applicationType
        binding.displayCardInfo.txtCodeSaleValue.text = codSale
        binding.displayCardInfo.txtFinalCardNumbers.text = pan
        binding.btnFinish.setOnClickListener{
            view.findNavController()
                .navigate(SucessPayMiniFragmentDirections.actionSucessPayMiniFragmentToAmountFragment())
        }
    }

    private fun getRandomCodSale() = Random.nextInt(99999)

}