package br.com.gertec.autostart.new_demo_pagamentos.acitivities

import android.os.Bundle
import android.widget.Button
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
    private val binding get() = _binding!!
    lateinit var mainViewModel: MainViewModel
    private lateinit var navController: NavController
    val outputCallbacks = OutputCallbacks(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupPPCompCommands()

        // Configurando o NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController
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


    fun setKeyboard(
        b1: Button,
        b2: Button,
        b3: Button,
        b4: Button,
        b5: Button,
        b6: Button,
        b7: Button,
        b8: Button,
        b9: Button,
        b0: Button,
        bCancel: Button,
        bConfirm: Button,
        bClear: Button,
        beep: Boolean
    ){
        CoroutineScope(Dispatchers.IO).launch{
//            mainViewModel.ppCompCommands.setPinKeyboard(
//                b1,b2,b3,b4,
//                b5,b6,b7,b8,
//                b9,b0,bCancel,bConfirm,
//                bClear,this@MainActivity,
//                beep
//            )
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

    //pepao
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navController.popBackStack(navController.graph.startDestinationId, false)
        mainViewModel.ppCompCommands.abort()
    }

}