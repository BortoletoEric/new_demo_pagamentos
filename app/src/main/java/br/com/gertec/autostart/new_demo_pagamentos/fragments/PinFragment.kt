package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentCheckCardBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentPinBinding

class PinFragment : Fragment() {
    private var _binding: FragmentPinBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}