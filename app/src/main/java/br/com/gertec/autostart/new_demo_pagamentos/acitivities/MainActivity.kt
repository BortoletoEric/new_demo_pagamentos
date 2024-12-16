package br.com.gertec.autostart.new_demo_pagamentos.acitivities

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
import br.com.gertec.autostart.new_demo_pagamentos.BuildConfig
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!

    lateinit var mainViewModel: MainViewModel
    private lateinit var navController: NavController
    lateinit var outputCallbacks: OutputCallbacks

    /******************** lifecycle functions ****************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupPPCompCommands()
        setupNavControler()

        // Configurando o NavController
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /******************** Other functions ****************************/

    private fun setupViewModel() {
        val viewModelProviderFactory = MainViewModelFactory()
        mainViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[MainViewModel::class.java]
    }

    private fun setupPPCompCommands() {
        outputCallbacks = OutputCallbacks(this@MainActivity)
        CoroutineScope(Dispatchers.IO).launch {
            mainViewModel.ppCompCommands.let {
                it.init(this@MainActivity)
                it.setDspCallback(outputCallbacks)
                it.open()
            }
        }
    }

    private fun setupNavControler() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    fun showSnackBar(msg: String, success: Boolean? = null) {
        val message = Snackbar.make(
            binding.mainContainer,
            msg,
            Snackbar.LENGTH_LONG
        )

        if (success == true) {
            message.setBackgroundTint(resources.getColor(R.color.successGreen))
        } else if (success == false) {
            message.setBackgroundTint(resources.getColor(R.color.errorRed))
        }

        message.show()
    }

    fun turnOnSimulatedLed(turnOn: Boolean){
        if(BuildConfig.FLAVOR == "gpos700"){
            if(turnOn) {
                binding.imageViewLedAzul.visibility = View.VISIBLE
            }else{
                binding.imageViewLedAzul.visibility = View.GONE
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val currentDestination = navController.currentDestination
        mainViewModel.keyPressed(keyCode)

        if (keyCode == 4) {
            if (currentDestination?.id == R.id.amountFragment) {
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navController.popBackStack(navController.graph.startDestinationId, false)
        mainViewModel.ppCompCommands.abort()
    }
}