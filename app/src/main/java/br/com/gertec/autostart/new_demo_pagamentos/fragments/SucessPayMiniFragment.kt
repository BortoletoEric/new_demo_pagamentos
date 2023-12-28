package br.com.gertec.autostart.new_demo_pagamentos.fragments

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.acitivities.MainActivity
import br.com.gertec.autostart.new_demo_pagamentos.commands.Utils
import br.com.gertec.autostart.new_demo_pagamentos.databinding.FragmentSucessPayMiniBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutCardInfoBinding
import br.com.gertec.autostart.new_demo_pagamentos.databinding.LayoutPaymentInfoMiniBinding
import br.com.gertec.gedi.exceptions.GediException
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.util.Locale
import kotlin.random.Random

class SucessPayMiniFragment : Fragment() {

    private var _binding: FragmentSucessPayMiniBinding? = null
    private val binding get() = _binding!!
    private lateinit var cardInfoBinding: LayoutCardInfoBinding
    private lateinit var paymentInfoBinding: LayoutPaymentInfoMiniBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var amount: String
    private lateinit var pan: String
    private lateinit var applicationType: String
    private lateinit var codSale: String
    private var numeroDeSerie: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSucessPayMiniBinding.inflate(inflater, container, false)

        cardInfoBinding = binding.displayCardInfo
        paymentInfoBinding = binding.displayPaymentInfo
        mainActivity = (activity as MainActivity)
        mainActivity.mainViewModel.postNs("")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amount = mainActivity.mainViewModel.transactionAmount
        pan = mainActivity.mainViewModel.pan
        mainActivity.mainViewModel.pan = ""
        applicationType = mainActivity.mainViewModel.applicationType
        codSale = getRandomCodSale().toString()

        setupViewsAndButtons(view)
        setupObservers()
        getNS()
    }

    private fun setupViewsAndButtons(view: View) {
        binding.displayPaymentInfo.txtAmount.text = amount
        binding.displayCardInfo.txtApplicationType.text = applicationType
        binding.displayCardInfo.txtCodeSaleValue.text = codSale
        binding.displayCardInfo.txtFinalCardNumbers.text = pan
        binding.btnFinish.setOnClickListener{
            view.findNavController()
                .navigate(SucessPayMiniFragmentDirections.actionSucessPayMiniFragmentToAmountFragment())
        }
    }

    private fun setupObservers() {
        mainActivity.mainViewModel.ns.observe(viewLifecycleOwner) {
            Thread.sleep(500)
            if (!it.isNullOrEmpty()) {
                numeroDeSerie = it

                binding.displayPaymentInfo.imageView3.setImageBitmap(
                    generateQRCode(
                        Utils.getPaymentReceiptQrCode(amount, applicationType, codSale, numeroDeSerie, getDeviceLanguage())
                    )
                )


            }
        }
    }

    private fun getNS() {
        try {
            numeroDeSerie = mainActivity.mainViewModel.getNs() ?: ""
            mainActivity.mainViewModel.postNs(numeroDeSerie)
        } catch (e: GediException) {
            mainActivity.mainViewModel.postNs(null)
        }
    }

    private fun getRandomCodSale() = Random.nextInt(99999)

    private fun getDeviceLanguage(): String {
        val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requireContext().resources.configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            requireContext().resources.configuration.locale
        }
        return locale.language
    }

    fun generateQRCode(input: String): Bitmap? {
        val width = 500
        val height = 500

        try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(input, BarcodeFormat.QR_CODE, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }

            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        return null
    }

}