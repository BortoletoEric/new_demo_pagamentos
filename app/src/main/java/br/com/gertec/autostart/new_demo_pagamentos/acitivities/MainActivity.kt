package br.com.gertec.autostart.new_demo_pagamentos.acitivities

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
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
import java.util.Locale

class MainActivity : AppCompatActivity(){
    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!
    lateinit var mainViewModel: MainViewModel
    private lateinit var navController: NavController
    lateinit var outputCallbacks : OutputCallbacks
    var pinActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupPPCompCommands()
        Log.d("msgg","mainActivity Created")
        // Configurando o NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

    }

    override fun onStart() {
        super.onStart()

        Handler(Looper.getMainLooper()).postDelayed({
            mainViewModel.setupGedi(this)
        },1500)

    }

    private fun setupPPCompCommands() {
        outputCallbacks = OutputCallbacks(this@MainActivity)
        CoroutineScope(Dispatchers.IO).launch{
            mainViewModel.ppCompCommands.let{
                it.init(this@MainActivity)
                it.setDspCallback(outputCallbacks)
                it.open()
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
        Log.d("msgg","mainActivity destroyed")
        _binding = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navController.popBackStack(navController.graph.startDestinationId, false)
        mainViewModel.ppCompCommands.abort()
    }


}