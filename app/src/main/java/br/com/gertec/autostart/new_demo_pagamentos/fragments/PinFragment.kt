package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentPinBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PinFragment : Fragment() {
    private var _binding: FragmentPinBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private val args: PinFragmentArgs by navArgs()
    private var transactionOk = false
    private var result: Pair<String?,String?>? = null

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

        setupViews(view)

        if (args.cardType != "03") { // Se for diferente de EMV com contato, não precisa chamar o GOC
            mainActivity.showSnackBar(getString(R.string.venda_finalizada_com_sucesso), true)
            mainActivity.mainViewModel.processCompleted("GOC")
        } else {
            goOnChip()
        }

        mainActivity.mainViewModel.processOk.observe(viewLifecycleOwner) { step ->
            Log.d("msgg","obs GOC... $step")
            when (step) {
                "GOC" -> {
                    if (BuildConfig.FLAVOR == "gpos700mini") {
                        view.findNavController().navigate(
                            PinFragmentDirections.actionPinFragmentToSucessPayMiniFragment(args.cardType)
                        )
                    } else {
                        view.findNavController().navigate(
                            PinFragmentDirections.actionPinFragmentToSucessPayFragment(args.cardType)
                        )
                    }
                }

                "GOC_NO_CARD" -> {
                    mainActivity.showSnackBar(getString(R.string.cart_o_removido), false)
                    view.findNavController().navigate(
                        PinFragmentDirections.actionPinFragmentToAmountFragment()
                    )
                }

                "GOC_TO" -> {
                    mainActivity.showSnackBar(getString(R.string.tempo_esgotado), false)
                }
            }
        }
    }

    private fun setupViews(view: View) {
        binding.txtPriceValue.text = mainActivity.mainViewModel.transactionAmount
        binding.removeCardContainer.visibility = View.GONE

        mainActivity.mainViewModel.display.observe(viewLifecycleOwner) { display -> //ATENÇÃO, ISSO ESTAVA GERANDO ERRO NO PIN KBD, ATENTE-SE AO MODIFICÁ-LO
            Log.d("msgg","obs flags... ${display[0]}")
            when (display[0]) {
                720896L, 724993L -> {
                    binding.removeCardContainer.visibility = View.VISIBLE
                    binding.tvFinalMessage.text = getString(R.string.retire_o_cart_o)
                }

                256L -> {
                    if (transactionOk) return@observe
                    if(result != null){
                        if(result!!.first == "GOC_ERR"){
                            mainActivity.showSnackBar(result!!.second?:getString(R.string.opera_o_cancelada), false)
                        }else{
                            mainActivity.showSnackBar(getString(R.string.opera_o_cancelada), false)
                        }
                    }

                    view.findNavController().navigate(
                        PinFragmentDirections.actionPinFragmentToAmountFragment()
                    )
                }

                512L -> {
                    if (BuildConfig.FLAVOR == "gpos760") {
                        binding.txtPin.text = display[2].toString()
                    }
                }
            }
        }
    }

    private fun goOnChip() {
        val am = fixAmountSize(args.amount)

        CoroutineScope(Dispatchers.IO).launch {
            mainActivity.mainViewModel.ppCompCommands.let {
                result = it.goOnChip(
                    "${am}000000000000011102000000000000000000000000000000001000003E820000003E880000",//${am}000000000000001321000000000000000000000000000000001000003E820000003E880000
                    "0019B",
                    "0119F0B1F813A9F6B9F6C9F66",
                    requireContext()
                )

//                if (!result.isNullOrEmpty()) {
//                    transactionOk = true
//                    mainActivity.showSnackBar(
//                        getString(R.string.venda_finalizada_com_sucesso),
//                        true
//                    )
//                    it.finishChip()
//                    mainActivity.mainViewModel.processCompleted("GOC")
//                }

                when (result?.first) {
                    "GOC_NO_CARD" -> mainActivity.mainViewModel.processCompleted("GOC_NO_CARD")
                    "GOC_TO" -> mainActivity.mainViewModel.processCompleted("GOC_TO")
                    "GOC_ERR" -> mainActivity.mainViewModel.processCompleted("GOC_ERR")
                    else -> {
                        if (!result?.first.isNullOrEmpty()) {
                            transactionOk = true
                            mainActivity.showSnackBar(
                                getString(R.string.venda_finalizada_com_sucesso),
                                true
                            )
                            Log.d("msgg","RESP GOC: $result")
                            it.finishChip()
                            mainActivity.mainViewModel.processCompleted("GOC")
                        }
                    }
                }
            }
        }
    }

    private fun fixAmountSize(amount: Long): String {
        val am = (amount / 100).toString()
        var tempAm = am
        for (i in 1..(12 - am.length)) {
            tempAm = "0$tempAm"
        }
        return tempAm
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}