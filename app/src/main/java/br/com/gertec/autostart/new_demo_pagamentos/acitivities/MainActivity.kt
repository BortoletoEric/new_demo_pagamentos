package br.com.gertec.autostart.new_demo_pagamentos.acitivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.callbacks.OutputCallbacks
import br.com.gertec.autostart.new_demo_pagamentos.databinding.ActivityMainBinding
import br.com.gertec.autostart.new_demo_pagamentos.viewmodels.MainViewModel
import br.com.gertec.autostart.new_demo_pagamentos.viewmodels.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(){
    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!
    lateinit var mainViewModel: MainViewModel
    private lateinit var navController: NavController
    val outputCallbacks = OutputCallbacks(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupPPCompCommands()
        setupKdb()
        //binding.touchPinKeyboard.visibility = View.GONE

        // Configurando o NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

//        binding.button1.setOnClickListener {
//            Log.d("msgg","btn1 estuporado")
//        }
    }

    private fun setupKdb() {
        mainViewModel.ppCompCommands.setPinKeyboard(
            binding.btn1Tag,binding.btn2Tag,binding.btn3Tag,binding.btn4Tag,
            binding.btn5Tag,binding.btn6Tag,binding.btn7Tag,binding.btn8Tag,
            binding.btn9Tag,binding.btn0Tag,binding.btnCancelTag,binding.btnEnterTag,
            binding.btnClearTag,this@MainActivity as Activity,true
        )
    }

    private fun setupPPCompCommands() {
        CoroutineScope(Dispatchers.IO).launch{
            mainViewModel.ppCompCommands.let{
                it.init(this@MainActivity)
                it.open()
                it.setDspCallback(outputCallbacks)
            }
        }
    }

    private fun setupViewModel() {
        val viewModelProviderFactory = MainViewModelFactory()
        mainViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[MainViewModel::class.java]
    }

    fun showSnackBar(msg: String, success: Boolean? = null){
        val message = Snackbar.make(
            binding.mainContainer,
            msg,
            Snackbar.LENGTH_LONG
        )

        if(success == true){
            message.setBackgroundTint(resources.getColor(R.color.successGreen))
        }else if(success == false){
            message.setBackgroundTint(resources.getColor(R.color.errorRed))
        }

        message.show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val currentDestination = navController.currentDestination
        mainViewModel.keyPressed(keyCode)

        if(keyCode == 4){
            if (currentDestination?.id == R.id.amountFragment) {
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navController.popBackStack(navController.graph.startDestinationId, false)
        mainViewModel.ppCompCommands.abort()
    }

    fun hidePinKeyboard(hide: Boolean) {
        if(hide){
            binding.pinKeyboard.visibility = View.INVISIBLE
        }else{
            Log.d("msgg","sAPARECA!")
            binding.pinKeyboard.visibility = View.VISIBLE
        }
    }

}