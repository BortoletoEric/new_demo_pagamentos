package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentCheckEventBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutKeyboardCkeBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat

class CheckEventFragment : Fragment() {
    private var _binding: FragmentCheckEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var keyboardBinding: LayoutKeyboardCkeBinding
    private lateinit var mainActivity: MainActivity
    private var currentFormattedAmount = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckEventBinding.inflate(inflater, container, false)

        keyboardBinding = binding.keyboardCke
        mainActivity = (activity as MainActivity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        Handler(Looper.getMainLooper()).postDelayed({
            setupPhysicalKbd(view)
            resetAllObservers()
        }, 1000)

        binding.keyboardCke.buttonConfirm.requestFocus()
    }

    private fun setupViews() {
        binding.txtPriceValue.setText(R.string.default_amount)

        binding.keyboardCke.let {
            it.button0.setOnClickListener { addDigitToAmount("0") }
            it.button1.setOnClickListener { addDigitToAmount("1") }
            it.button2.setOnClickListener { addDigitToAmount("2") }
            it.button3.setOnClickListener { addDigitToAmount("3") }
            it.button4.setOnClickListener { addDigitToAmount("4") }
            it.button5.setOnClickListener { addDigitToAmount("5") }
            it.button6.setOnClickListener { addDigitToAmount("6") }
            it.button7.setOnClickListener { addDigitToAmount("7") }
            it.button8.setOnClickListener { addDigitToAmount("8") }
            it.button9.setOnClickListener { addDigitToAmount("9") }
            it.buttonClear.setOnClickListener {
                binding.txtPriceValue.setText(R.string.default_amount).toString()
            }
            it.buttonConfirm.setOnClickListener { goToCardTypeFragment(it) }
        }

        binding.txtPriceValue.movementMethod = null
        binding.txtPriceValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, i1: Int, i2: Int) {
                if (s.toString() != currentFormattedAmount) {
                    binding.txtPriceValue.removeTextChangedListener(this)
                    val currentAmount: Double = getAmount(s)
                    currentFormattedAmount = formatAmount(currentAmount).toString()
                    binding.txtPriceValue.text = currentFormattedAmount
                    binding.txtPriceValue.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun resetAllObservers() {
        mainActivity.mainViewModel.processCompleted("")
    }

    private fun setupPhysicalKbd(view: View) {
        mainActivity.mainViewModel.kbdKeyPressed.observe(viewLifecycleOwner) { keyCode ->
            when (keyCode) {
                8 -> {
                    addDigitToAmount("1")
                }//1
                9 -> {
                    addDigitToAmount("2")
                }//2
                10 -> {
                    addDigitToAmount("3")
                }//3
                11 -> {
                    addDigitToAmount("4")
                }//4
                12 -> {
                    addDigitToAmount("5")
                }//5
                13 -> {
                    addDigitToAmount("6")
                }//6
                14 -> {
                    addDigitToAmount("7")
                }//7
                15 -> {
                    addDigitToAmount("8")
                }//8
                16 -> {
                    addDigitToAmount("9")
                }//9
                7 -> {
                    addDigitToAmount("0")
                }//0
                4 -> {
                    binding.txtPriceValue.setText(R.string.default_amount).toString()
                }//anula
                67 -> {
                    binding.txtPriceValue.setText(R.string.default_amount).toString()
                }//limpa
                66 -> {
                    goToCardTypeFragment(view)
                }//enter
                170 -> Unit
            }
        }
    }

    private fun addDigitToAmount(digit: String) {
        val currentAmount: String = binding.txtPriceValue.text.toString()
        if (currentAmount.length < 9) {
            binding.txtPriceValue.text = String.format("%s%s", currentAmount, digit)
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
        val amount = getAmount(binding.txtPriceValue.text)

        if (amount > 0) {
            mainActivity.mainViewModel.transactionAmount = currentFormattedAmount
            view.findNavController().navigate(
                CheckEventFragmentDirections.actionCheckEventFragmentToCardTypeFragment(
                    (amount).toLong(),
                    true
                )
            )
        } else {
            mainActivity.showSnackBar(getString(R.string.digite_o_valor), false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}