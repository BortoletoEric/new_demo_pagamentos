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
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentCheckCardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CheckCardFragment : Fragment() {
    private var _binding: FragmentCheckCardBinding? = null
    private val binding get() = _binding!!
    private val args: CheckCardFragmentArgs by navArgs()
    private lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCheckCardBinding.inflate(inflater, container, false)
        mainActivity =(activity as MainActivity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCard()

        mainActivity.mainViewModel.processOk.observe(viewLifecycleOwner){ step ->
            when(step){
                "GCR" -> {}
            }
        }

    }

    private fun getCard() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(1_000)
            mainActivity.mainViewModel.ppCompCommands.let{
                it.open()
                val result = it.getCard("0099000000001000231122121636135799753100")//0099000000000100231122115800135799753100
                //0099000000001000231122121636135799753100
                Log.d("msgg","grande susto: $result")
                it.close("Obrigado")

                mainActivity.mainViewModel.processCompleted("GCR")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}