package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Build
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
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentAmountBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutDisplayKeyboardBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutKeyboardBinding
import br.com.gertec.gedi.GEDI
import br.com.gertec.gedi.interfaces.IGEDI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private lateinit var mainActivity: MainActivity
    private var currentFormattedAmount = ""
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var iGedi: GEDI

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAmountBinding.inflate(inflater, container, false)

        displayKeyboardBinding = binding.displayKeyboard
        keyboardBinding = binding.keyboard
        mainActivity = (activity as MainActivity)
        lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.keyboard.buttonConfirm.requestFocus()

        setupViews()

        Handler(Looper.getMainLooper()).postDelayed({
            resetAllObservers()
            setupObservers(view)
            setupPPCompLanguage()

            checkEvent()
            if (hasPhysicalKbd()) {
                setupPhysicalKbd(view)
            }
        }, 1500)
    }

    private fun setupPPCompLanguage() {
        CoroutineScope(Dispatchers.IO).launch {
            mainActivity.mainViewModel.ppCompCommands.selectLanguage(getDeviceLanguage())
        }
    }

    private fun getDeviceLanguage(): String {
        val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            resources.configuration.locale
        }
        return locale.language
    }

    private fun resetAllObservers() {
        mainActivity.mainViewModel.processCompleted("")
        mainActivity.mainViewModel.updateDisplay(-999L, "", "")
        mainActivity.mainViewModel.keyPressed(-9)
    }

    private fun setupObservers(view: View) {
        mainActivity.mainViewModel.processOk.observe(lifecycleOwner){
            when(it){
                "CKE" -> {
                    val amount = getAmount(binding.displayKeyboard.txtPriceValue.text)
                    if (amount == 0.0) {
                        initGedi()
                        view.findNavController().navigate(
                            AmountFragmentDirections.actionAmountFragmentToCheckEventFragment()
                        )

                    }else{
                        mainActivity.mainViewModel.transactionAmount = currentFormattedAmount
                        initGedi()
                        view.findNavController().navigate(
                            AmountFragmentDirections.actionAmountFragmentToCardTypeFragment(
                                (amount).toLong(),
                                true
                            )
                        )
                    }
                }

                "CKE_MC_ERR" -> {
                    checkEvent()
                    mainActivity.showSnackBar(getString(R.string.erro_na_leitura_do_cart_o), false)
                }
            }
        }
    }

    private fun setupPhysicalKbd(view: View) {
        mainActivity.mainViewModel.kbdKeyPressed.observe(lifecycleOwner){ keyCode ->
            Log.d("AmountFragment", "KeyPress: $keyCode")
            when(keyCode){
                8 -> {addDigitToAmount("1")}//1
                9 -> {addDigitToAmount("2")}//2
                10 -> {addDigitToAmount("3")}//3
                11 -> {addDigitToAmount("4")}//4
                12 -> {addDigitToAmount("5")}//5
                13 -> {addDigitToAmount("6")}//6
                14 -> {addDigitToAmount("7")}//7
                15 -> {addDigitToAmount("8")}//8
                16 -> {addDigitToAmount("9")}//9
                7 -> {addDigitToAmount("0")}//0
                4 -> {binding.displayKeyboard.txtPriceValue.setText(R.string.default_amount).toString()}//anula
                67 -> {binding.displayKeyboard.txtPriceValue.setText(R.string.default_amount).toString()}//limpa
                66 -> {goToCardTypeFragment(view)}//enter
                170 -> Unit
            }
        }
    }

    private fun setupViews() {
        binding.displayKeyboard.txtPriceValue.setText(R.string.default_amount)

        binding.keyboard.button0.setOnClickListener { addDigitToAmount("0") }
        binding.keyboard.button1.setOnClickListener { addDigitToAmount("1") }
        binding.keyboard.button2.setOnClickListener { addDigitToAmount("2") }
        binding.keyboard.button3.setOnClickListener { addDigitToAmount("3") }
        binding.keyboard.button4.setOnClickListener { addDigitToAmount("4") }
        binding.keyboard.button5.setOnClickListener { addDigitToAmount("5") }
        binding.keyboard.button6.setOnClickListener { addDigitToAmount("6") }
        binding.keyboard.button7.setOnClickListener { addDigitToAmount("7") }
        binding.keyboard.button8.setOnClickListener { addDigitToAmount("8") }
        binding.keyboard.button9.setOnClickListener { addDigitToAmount("9") }
        binding.keyboard.buttonClear.setOnClickListener {
            binding.displayKeyboard.txtPriceValue.setText(R.string.default_amount).toString()
        }
        binding.keyboard.buttonConfirm.setOnClickListener {it ->
                goToCardTypeFragment(it)
        }

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

                Log.d("AmountFragment", "TextWatcher: $currentFormattedAmount")

            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun checkEvent() {
        CoroutineScope(Dispatchers.IO).launch {
            val resp =
                mainActivity.mainViewModel.ppCompCommands.checkEvent("0110") //magnético e chip apenas
            if (!resp.isNullOrEmpty()) {
                if (resp == "CKE_MC_ERR") {
                    mainActivity.mainViewModel.processCompleted(resp)
                } else {
                    mainActivity.mainViewModel.processCompleted("CKE")
                }
            }
        }
    }

    private fun initGedi(){
        mainActivity.mainViewModel.setupGedi(mainActivity.applicationContext)
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
        Log.i("AmountFragment", "Valor amount: $amount")

        if (amount > 0) {
            mainActivity.mainViewModel.transactionAmount = currentFormattedAmount
            initGedi()
            Log.i("AmountFragment", "Instância GEDI iniciada: $amount")
            view.findNavController().navigate(
                AmountFragmentDirections.actionAmountFragmentToCardTypeFragment(
                    (amount).toLong(),
                    false
                )
            )
        } else {
            mainActivity.showSnackBar(getString(R.string.digite_o_valor), false)
        }
    }

    private fun hasPhysicalKbd(): Boolean {
        return Build.MODEL.equals("GPOS760")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}