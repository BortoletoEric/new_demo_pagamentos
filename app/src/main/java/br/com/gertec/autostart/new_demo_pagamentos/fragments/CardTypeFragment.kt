package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentCardTypeBinding

class CardTypeFragment : Fragment() {

    private var _binding: FragmentCardTypeBinding? = null
    private val binding get() = _binding!!
    private val args: CardTypeFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardTypeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtons()

    }

    private fun setupButtons() {
        binding.button1.setOnClickListener {
            it.findNavController().navigate(
                CardTypeFragmentDirections.actionCardTypeFragmentToCheckCardFragment(args.amount,"")
            )
        }
        binding.button2.setOnClickListener {
            it.findNavController().navigate(
                CardTypeFragmentDirections.actionCardTypeFragmentToCheckCardFragment(args.amount,"")
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}