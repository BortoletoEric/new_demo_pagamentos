package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentAmountBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutDisplayKeyboardBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutKeyboardBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat


class AmountFragment : Fragment() {

    private var _binding: FragmentAmountBinding? = null
    private val binding get() = _binding!!
    private lateinit var displayKeyboardBinding: LayoutDisplayKeyboardBinding
    private lateinit var keyboardBinding: LayoutKeyboardBinding

    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAmountBinding.inflate(inflater, container, false)

        displayKeyboardBinding = binding.displayKeyboard
        keyboardBinding = binding.keyboard
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view);

        binding.displayKeyboard.txtPriceValue.setText(R.string.default_amount);

        binding.keyboard.button0.setOnClickListener(View.OnClickListener { addDigitToAmount("0"); })
        binding.keyboard.button1.setOnClickListener(View.OnClickListener { addDigitToAmount("1"); })
        binding.keyboard.button2.setOnClickListener(View.OnClickListener { addDigitToAmount("2"); })
        binding.keyboard.button3.setOnClickListener(View.OnClickListener { addDigitToAmount("3"); })
        binding.keyboard.button4.setOnClickListener(View.OnClickListener { addDigitToAmount("4"); })
        binding.keyboard.button5.setOnClickListener(View.OnClickListener { addDigitToAmount("5"); })
        binding.keyboard.button6.setOnClickListener(View.OnClickListener { addDigitToAmount("6"); })
        binding.keyboard.button7.setOnClickListener(View.OnClickListener { addDigitToAmount("7"); })
        binding.keyboard.button8.setOnClickListener(View.OnClickListener { addDigitToAmount("8"); })
        binding.keyboard.button9.setOnClickListener(View.OnClickListener { addDigitToAmount("9"); })
        binding.keyboard.buttonClear.setOnClickListener(View.OnClickListener { binding.displayKeyboard.txtPriceValue.setText(
            R.string.default_amount
        ).toString() })

        binding.displayKeyboard.txtPriceValue.setMovementMethod(null)
        binding.displayKeyboard.txtPriceValue.addTextChangedListener(object : TextWatcher {
            private var currentFormattedAmount = ""
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, i1: Int, i2: Int) {
                if (s.toString() != currentFormattedAmount) {
                    binding.displayKeyboard.txtPriceValue.removeTextChangedListener(this)
                    val currentAmount: Double = getAmount(s)
                    currentFormattedAmount = formatAmount(currentAmount).toString()
                    binding.displayKeyboard.txtPriceValue.setText(currentFormattedAmount)
                    binding.displayKeyboard.txtPriceValue.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun addDigitToAmount(digit: String) {
        val currentAmount: String = binding.displayKeyboard.txtPriceValue.getText().toString()
        if (currentAmount.length < 9) {
            binding.displayKeyboard.txtPriceValue.setText(String.format("%s%s", currentAmount, digit))
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
        val symbols: DecimalFormatSymbols = decimalFormat.getDecimalFormatSymbols()
        symbols.setCurrencySymbol("")
        decimalFormat.setDecimalFormatSymbols(symbols)
        return decimalFormat.format(amount / 100)
    }
}