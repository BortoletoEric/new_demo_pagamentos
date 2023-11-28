package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentAmountBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutDisplayKeyboardBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutKeyboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

class AmountFragment : Fragment() {

    private var _binding: FragmentAmountBinding? = null
    private val binding get() = _binding!!
    private lateinit var displayKeyboardBinding: LayoutDisplayKeyboardBinding
    private lateinit var keyboardBinding: LayoutKeyboardBinding
    private var amount: Long = 0
    private lateinit var mainActivity: MainActivity
    private var currentFormattedAmount = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAmountBinding.inflate(inflater, container, false)

        displayKeyboardBinding = binding.displayKeyboard
        keyboardBinding = binding.keyboard
        mainActivity = (activity as MainActivity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.displayKeyboard.txtPriceValue.setText(R.string.default_amount)

        binding.keyboard.button0.setOnClickListener{ addDigitToAmount("0") }
        binding.keyboard.button1.setOnClickListener{ addDigitToAmount("1") }
        binding.keyboard.button2.setOnClickListener{ addDigitToAmount("2") }
        binding.keyboard.button3.setOnClickListener{ addDigitToAmount("3") }
        binding.keyboard.button4.setOnClickListener{ addDigitToAmount("4") }
        binding.keyboard.button5.setOnClickListener{ addDigitToAmount("5") }
        binding.keyboard.button6.setOnClickListener{ addDigitToAmount("6") }
        binding.keyboard.button7.setOnClickListener{ addDigitToAmount("7") }
        binding.keyboard.button8.setOnClickListener{ addDigitToAmount("8") }
        binding.keyboard.button9.setOnClickListener{ addDigitToAmount("9") }
        binding.keyboard.buttonClear.setOnClickListener{
            binding.displayKeyboard.txtPriceValue.setText(R.string.default_amount).toString()
        }
        binding.keyboard.buttonConfirm.setOnClickListener{ goToCardTypeFragment(it) }

        binding.displayKeyboard.txtPriceValue.movementMethod = null
        binding.displayKeyboard.txtPriceValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, i1: Int, i2: Int) {
                if (s.toString() != currentFormattedAmount) {
                    binding.displayKeyboard.txtPriceValue.removeTextChangedListener(this)
                    val currentAmount: Double = getAmount(s)
                    currentFormattedAmount = formatAmount(currentAmount).toString()
                    binding.displayKeyboard.txtPriceValue.text = currentFormattedAmount
                    binding.displayKeyboard.txtPriceValue.addTextChangedListener(this)
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })

        Handler(Looper.getMainLooper()).postDelayed({
            checkEvent()
        },1000)


//        mainActivity.mainViewModel.processOk.observe(viewLifecycleOwner){
//            when(it){
//                "CKE" -> {
//                    view.findNavController().navigate(
//                        AmountFragmentDirections.actionAmountFragmentToCardTypeFragment(19000)
//                    )
//                }
//            }
//        }
    }

    private fun checkEvent(){
        CoroutineScope(Dispatchers.IO).launch {
            mainActivity.mainViewModel.ppCompCommands.checkEvent("0110") //magnético, chip e ctlss apenas
            mainActivity.mainViewModel.processCompleted("CKE")
        }
    }

    private fun addDigitToAmount(digit: String) {
        val currentAmount: String = binding.displayKeyboard.txtPriceValue.text.toString()
        if (currentAmount.length < 9) {
            binding.displayKeyboard.txtPriceValue.text = String.format("%s%s", currentAmount, digit)
        }
    }

    private fun getAmount(string: CharSequence): Double {
        val regex = "[,.]"
        var cleanString = string.toString().replace(regex.toRegex(), "")
        cleanString = cleanString.replace("\\s+".toRegex(), "")
        return cleanString.toDouble()
    }

    private fun formatAmount(amount: Double): String? {
        val decimalFormat: DecimalFormat = NumberFormat.getCurrencyInstance() as DecimalFormat
        val symbols: DecimalFormatSymbols = decimalFormat.decimalFormatSymbols
        symbols.currencySymbol = ""
        decimalFormat.decimalFormatSymbols = symbols
        return decimalFormat.format(amount / 100)
    }

    private fun goToCardTypeFragment(view: View) {
        val amount = getAmount(binding.displayKeyboard.txtPriceValue.text)

        if (amount > 0) {
            mainActivity.mainViewModel.transactionAmount = currentFormattedAmount

            view.findNavController().navigate(
                AmountFragmentDirections.actionAmountFragmentToCardTypeFragment((amount).toLong())
            )
        } else {
            showDialogEmptyAmount()
        }
    }

    private fun showDialogEmptyAmount() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Por favor, remova o cartão!")
            .setMessage("O valor não pode estar vazio, digite um valor e insira novamente o cartão.")
        builder.setCancelable(false)
        builder.setPositiveButton(
            "OK"
        ) { _, _ -> onResume() }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}