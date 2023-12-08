package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentCheckCardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class CheckCardFragment : Fragment() {
    private var _binding: FragmentCheckCardBinding? = null
    private val binding get() = _binding!!
    private val args: CheckCardFragmentArgs by navArgs()
    private lateinit var mainActivity: MainActivity
    private var cardType = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCheckCardBinding.inflate(inflater, container, false)
        mainActivity = (activity as MainActivity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        getCard()
        //TESTE
//        Handler(Looper.getMainLooper()).postDelayed({
//            view.findNavController().navigate(
//                CheckCardFragmentDirections.actionCheckCardFragmentToSucessPayFragment(cardType)
//            )
//        }, 1800)


        mainActivity.mainViewModel.processOk.observe(viewLifecycleOwner) { step ->
            Log.d("msgg", "process obs: $step")
            when (step) {
                "GCR" -> {
                    Log.d("msgg", "insite gcr step...")
                    if (cardType != "03") {
                        Log.d("msgg", "navigate init...")

                        Log.i("TAG", "FLAVOR: " + BuildConfig.FLAVOR)

                        if (BuildConfig.FLAVOR.equals("gpos700mini")) {
                            view.findNavController().navigate(
                                CheckCardFragmentDirections.actionCheckCardFragmentToSucessPayMiniFragment(
                                    cardType
                                )
                            )
                        } else {
                            view.findNavController().navigate(
                                CheckCardFragmentDirections.actionCheckCardFragmentToSucessPayFragment(
                                    cardType
                                )
                            )
                        }
                    } else {
                        Log.d("msgg", "navc GCR: ${view.findNavController()}")
                        view.findNavController().navigate(
                            CheckCardFragmentDirections.actionCheckCardFragmentToPinFragment(
                                args.amount,
                                cardType
                            )
                        )
                    }

                }

                "GCR_ERROR" -> {
                    view.findNavController().navigate(

                        CheckCardFragmentDirections.actionCheckCardFragmentToAmountFragment()
                    )
                }
            }
        }

    }

    private fun setupViews() {
        mainActivity.mainViewModel.display.observe(viewLifecycleOwner) { display ->
            binding.textView.text = "${display[1]}\n${display[2]}" //usar logica dos flags
        }
    }



    private fun getCard() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(1_000)
            mainActivity.mainViewModel.ppCompCommands.let {
                if (!args.isCke) {
                    it.abort() //pegar dados automaticamente
                }
                val result = it.getCard(
                    "0002${fixAmountInput(args.amount)}231122121636012345678900",
                    requireContext()
                )//0099000000000100231122115800135799753100

                if (!result.second.isNullOrEmpty()) {
                    if (!result.first) {
                        mainActivity.mainViewModel.processCompleted("GCR_ERROR")
                        mainActivity.showSnackBar(result.second!!, false)
                    } else {
                        try {
                            val pan = result.second!!.substring(234, 253)
                            cardType = result.second!!.substring(0, 2)
                            mainActivity.mainViewModel.pan = hidePan(pan.trim()) ?: ""
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        mainActivity.mainViewModel.processCompleted("GCR")
                    }
                }

            }
        }
    }

    private fun fixAmountInput(amount: Long): String {
        var strAmount = amount.toString()
        val len = strAmount.length
        for (i in 1..12 - len) {
            strAmount = "0$strAmount"
        }
        return strAmount
    }

    private fun hidePan(pan: String?): String? {
        if (pan.isNullOrEmpty()) return null
        return pan.substring(pan.length - 4, pan.length)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}