package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentCardTypeBinding


class CardTypeFragment : Fragment() {

    private var _binding: FragmentCardTypeBinding? = null
    private val binding get() = _binding!!
    private val args: CardTypeFragmentArgs by navArgs()
    private lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardTypeBinding.inflate(inflater, container, false)
        mainActivity = (activity as MainActivity)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtons()
        resetAllObservers()
    }

    private fun resetAllObservers() {
        mainActivity.mainViewModel.processCompleted("")
    }

    private fun setupButtons() {
        binding.button1.setOnClickListener {
            mainActivity.mainViewModel.applicationType = "Débito"
            it.findNavController().navigate(
                CardTypeFragmentDirections.actionCardTypeFragmentToCheckCardFragment(args.amount,"02",args.isCke)
            )
        }
        binding.button2.setOnClickListener {
            mainActivity.mainViewModel.applicationType = "Crédito"
            it.findNavController().navigate(
                CardTypeFragmentDirections.actionCardTypeFragmentToCheckCardFragment(args.amount,"01",args.isCke)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}