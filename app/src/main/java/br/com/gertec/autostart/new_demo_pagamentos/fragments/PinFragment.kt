package br.com.gertec.autostart.new_demo_pagamentos.fragments

//import br.com.setis.gertec.bibliotecapinpad.BuildConfig //gpos720 apenas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.PinKbdActivity
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentPinBinding
import br.com.gertec.gedi.GEDI
import br.com.gertec.gedi.interfaces.IGEDI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PinFragment : Fragment() {
    private var _binding: FragmentPinBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    var pin = ""
    private val args: PinFragmentArgs by navArgs()
    private var transactionOk = false
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
        setupButtons()
        goOnChip()

        mainActivity.mainViewModel.processOk.observe(viewLifecycleOwner) { step ->

            when (step) {
                "GOC" -> {
                    view.findNavController().navigate(
                        PinFragmentDirections.actionPinFragmentToSucessPayFragment(args.cardType)
                    )
                }

                "GOC_NO_CARD" -> {
                    mainActivity.showSnackBar("Cartão removido", false)
                    view.findNavController().navigate(
                        PinFragmentDirections.actionPinFragmentToAmountFragment()
                    )
                }

                "GOC_TO" -> {
                    mainActivity.showSnackBar("TEMPO ESGOTADO", false)
                    view.findNavController().navigate(
                        PinFragmentDirections.actionPinFragmentToAmountFragment()
                    )
                }
            }
        }
    }

    private fun setupViews(view: View) {
        binding.txtPriceValue.text = mainActivity.mainViewModel.transactionAmount
        binding.removeCardContainer.visibility = View.GONE

        mainActivity.mainViewModel.display.observe(viewLifecycleOwner) { display ->

            when (display[0]) {
                512L -> {
                    Log.d("msgg","obs 512")
                    if (display[2].toString().length <= 4) {
                        val iGedi: IGEDI = GEDI.getInstance(context)
                        iGedi.audio.Beep()
                        binding.txtPin.text = display[2].toString()
                    }
                }

                720896L -> {
                    Log.d("msgg","obs 720896")
                    binding.removeCardContainer.visibility = View.VISIBLE
                    binding.tvFinalMessage.text = "RETIRE O CARTÃO"
                }

                256L -> {
                    Log.d("msgg","obs 256")
                    if (transactionOk) return@observe
                    mainActivity.showSnackBar("OPERAÇÃO CANCELADA", false)
                    view.findNavController().navigate(
                        PinFragmentDirections.actionPinFragmentToAmountFragment()
                    )
                }
                917504L -> {
                    Log.d("msgg","obs 917504")
                    if (BuildConfig.FLAVOR == "gpos760") return@observe
                    mainActivity.mainViewModel.ppCompCommands.showKBD(PinKbdActivity(), mainActivity, requireContext())
                }
                else -> {
                    binding.txtPin.text
                }
            }
        }

    }
    private fun setupButtons() {
//        binding.button0.setOnClickListener { addDigitToPin("0") }
//        binding.button1.setOnClickListener { addDigitToPin("1") }
//        binding.button2.setOnClickListener { addDigitToPin("2") }
//        binding.button3.setOnClickListener { addDigitToPin("3") }
//        binding.button4.setOnClickListener { addDigitToPin("4") }
//        binding.button5.setOnClickListener { addDigitToPin("5") }
//        binding.button6.setOnClickListener { addDigitToPin("6") }
//        binding.button7.setOnClickListener { addDigitToPin("7") }
//        binding.button8.setOnClickListener { addDigitToPin("8") }
//        binding.button9.setOnClickListener { addDigitToPin("9") }
//        binding.buttonClear.setOnClickListener {
//            pin = ""
//            binding.txtPin.text = ""
//        }
//        binding.buttonErase.setOnClickListener { erasePin() }
//        binding.buttonConfirm.setOnClickListener {
//            //goOnChip()
//        }

    }

    private fun erasePin() {
        if (pin.isEmpty()) return
        var pinTv = ""
        pin = pin.substring(0, pin.length - 1)
        pin.forEach { _ ->
            pinTv += "*"
        }
        binding.txtPin.text = pinTv
    }

    private fun addDigitToPin(digit: String) {
        var pinTv = ""
        pin += digit
        pin.forEach { _ ->
            pinTv += "*"
        }
        binding.txtPin.text = pinTv
    }

    private fun goOnChip() {
        if (args.cardType != "03") { // Se for diferente de EMV com contato, não precisa chamar o GOC
            mainActivity.showSnackBar("Venda finalizada com sucesso!", true)
            mainActivity.mainViewModel.processCompleted("GOC")
            return
        }

        val am = fixAmountSize(args.amount)
        var result: String? = ""

        CoroutineScope(Dispatchers.IO).launch {
            mainActivity.mainViewModel.ppCompCommands.let {
                result = it.goOnChip(
                    "${am}000000000000001101000000000000000000000000000000001000003E820000003E880000",//${am}000000000000001321000000000000000000000000000000001000003E820000003E880000
                    "0019B",
                    "0119F0B1F813A9F6B9F6C9F66",
                    requireContext()
                )//Term floor lim: 000003E8 =
                Log.d("msgg","G0C RES: $result")
                if(result == "GOC_NO_CARD"){
                    mainActivity.mainViewModel.processCompleted("GOC_NO_CARD")
                } else if (result == "GOC_TO") {
                    mainActivity.mainViewModel.processCompleted("GOC_TO")
                } else if (!result.isNullOrEmpty()) {
                    transactionOk = true
                    mainActivity.showSnackBar("Venda finalizada com sucesso!", true)
                    it.finishChip()
                    mainActivity.mainViewModel.processCompleted("GOC")
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