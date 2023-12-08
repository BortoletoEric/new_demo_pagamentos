package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentCardTypeBinding
import br.com.gertec.gedi.GEDI
import br.com.gertec.gedi.interfaces.IGEDI
import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig //gpos760 apenas
import java.util.Locale

//import br.com.setis.gertec.bibliotecapinpad.BuildConfig //gpos720 apenas

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
        if (hasPhysicalKbd()) {
            binding.button1.background = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_button_credito_dgt1)
            binding.button2.background = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_button_credito_dgt2)
            setupPhysicalKbd(view)
        }

    }

    private fun resetAllObservers() {
        mainActivity.mainViewModel.processCompleted("")
    }

    private fun setupButtons() {
        binding.button1.setOnClickListener {
            startCheckCardFragment(getString(R.string.d_bito), it)
        }
        binding.button2.setOnClickListener {
            startCheckCardFragment(getString(R.string.cr_dito), it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupPhysicalKbd(view: View) {
        mainActivity.mainViewModel.kbdKeyPressed.observe(viewLifecycleOwner) { keyCode ->
            when (keyCode) {
                8 -> {
                    startCheckCardFragment(getString(R.string.d_bito), view)
                    beep()
                }//1
                9 -> {
                    startCheckCardFragment(getString(R.string.cr_dito), view)
                    beep()
                }//2
            }
        }
    }

    private fun startCheckCardFragment(applicationType: String, view: View) {

        mainActivity.mainViewModel.applicationType = applicationType
        if (applicationType == getString(R.string.d_bito)) {
            view.findNavController().navigate(
                CardTypeFragmentDirections.actionCardTypeFragmentToCheckCardFragment(
                    args.amount,
                    "02",
                    args.isCke
                )
            )
        } else {
            view.findNavController().navigate(
                CardTypeFragmentDirections.actionCardTypeFragmentToCheckCardFragment(
                    args.amount,
                    "01",
                    args.isCke
                )
            )
        }
    }

    private fun beep() {
        val iGedi: IGEDI = GEDI.getInstance(context)
        iGedi.audio.Beep()
    }

    private fun hasPhysicalKbd(): Boolean {
        return BuildConfig.FLAVOR == "gpos760"
    }
}