package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentPinBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class PinFragment : Fragment() {
    private var _binding: FragmentPinBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    var pin = ""
    private val args: PinFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPinBinding.inflate(inflater, container, false)
        mainActivity = (activity as MainActivity)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupButtons()
        goOnChip()

        mainActivity.mainViewModel.processOk.observe(viewLifecycleOwner){ step ->

            when(step){
                "GOC" -> {
                    view.findNavController().navigate(
                        PinFragmentDirections.actionPinFragmentToSucessPayFragment()
                    )
                }
                "GOC_NO_CARD" -> {
                    mainActivity.showSnackBar("Cartão removido")
                    view.findNavController().navigate(
                        PinFragmentDirections.actionPinFragmentToAmountFragment()
                    )
                }
            }
        }
    }

    private fun setupViews() {
        binding.txtPriceValue.text = mainActivity.mainViewModel.transactionAmount
//        GPOS760 NÃO
        with(binding){
            mainActivity.setKeyboard(
                button1,
                button2,
                button3,
                button4,
                button5,
                button6,
                button7,
                button8,
                button9,
                button0,
                buttonErase,
                buttonConfirm,
                buttonClear,
                true
            )
        }

    }


    private fun setupButtons() {
        binding.button0.setOnClickListener{ addDigitToPin("0") }
        binding.button1.setOnClickListener{ addDigitToPin("1") }
        binding.button2.setOnClickListener{ addDigitToPin("2") }
        binding.button3.setOnClickListener{ addDigitToPin("3") }
        binding.button4.setOnClickListener{ addDigitToPin("4") }
        binding.button5.setOnClickListener{ addDigitToPin("5") }
        binding.button6.setOnClickListener{ addDigitToPin("6") }
        binding.button7.setOnClickListener{ addDigitToPin("7") }
        binding.button8.setOnClickListener{ addDigitToPin("8") }
        binding.button9.setOnClickListener{ addDigitToPin("9") }
        binding.buttonClear.setOnClickListener{
            pin = ""
            binding.txtPin.text = ""
        }
        binding.buttonErase.setOnClickListener{ erasePin() }
        binding.buttonConfirm.setOnClickListener{
            //goOnChip()
        }

    }


    private fun formatAmount(amount: Long): CharSequence? {
        return DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("pt","BR"))).format(amount/100)
    }

    private fun erasePin() {
        if(pin.isEmpty()) return
        var pinTv = ""
        pin = pin.substring(0, pin.length-1)
        pin.forEach{ _ ->
            pinTv += "*"
        }
        binding.txtPin.text = pinTv
    }

    private fun addDigitToPin(digit: String) {
        var pinTv = ""
        pin += digit
        pin.forEach{ _ ->
            pinTv += "*"
        }
        binding.txtPin.text = pinTv
    }

    private fun goOnChip() {
        val vai = fixAmountSize(args.amount)
        var result: String? = ""
        CoroutineScope(Dispatchers.IO).launch {
            mainActivity.mainViewModel.ppCompCommands.let{
                result = it.goOnChip(
                    "${vai}000000000000001321000000000000000000000000000000001000003E820000003E880000",
                    "0019B",
                    "0119F0B1F813A9F6B9F6C9F66"
                )

                if(result == "GOC_NO_CARD"){
                    mainActivity.mainViewModel.processCompleted("GOC_NO_CARD")
                }else if(!result.isNullOrEmpty()){
                    it.finishChip()
                    mainActivity.mainViewModel.processCompleted("GOC")
                }
            }
        }
    }

    private fun fixAmountSize(amount: Long): String {
        val am = (amount/100).toString()
        var tempAm = am
        for(i in 1..(12 - am.length)){
            tempAm = "0$tempAm"
        }
        return tempAm
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}