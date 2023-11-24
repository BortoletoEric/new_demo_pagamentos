package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentSucessPayBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutCardInfoBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutPaymentInfoBinding

class SucessPayFragment : Fragment() {

    private var _binding: FragmentSucessPayBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardInfoBinding: LayoutCardInfoBinding
    private lateinit var paymentInfoBinding: LayoutPaymentInfoBinding
    private lateinit var mainActivity: MainActivity
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

        setupViews()

    }

    private fun setupViews() {
        binding.displayPaymentInfo.txtAmount.text = mainActivity.mainViewModel.transactionAmount
        binding.displayCardInfo.txtFinalCardNumbers.text = mainActivity.mainViewModel.pan
        binding.displayCardInfo.txtApplicationType.text = mainActivity.mainViewModel.applicationType
    }
}