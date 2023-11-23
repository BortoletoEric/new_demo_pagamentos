package br.com.gertec.autostart.new_demo_pagamentos.acitivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import br.com.gertec.autostart.new_demo_pagamentos.R
import br.com.gertec.autostart.new_demo_pagamentos.databinding.ActivityMainBinding
import br.com.gertec.autostart.new_demo_pagamentos.viewmodels.MainViewModel
import br.com.gertec.autostart.new_demo_pagamentos.viewmodels.MainViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    lateinit var mainViewModel: MainViewModel
    private lateinit var navController: NavController

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
        CoroutineScope(Dispatchers.IO).launch {
            mainViewModel.ppCompCommands.init(this@MainActivity)
        }
    }

    private fun setupViewModel() {
        val viewModelProviderFactory = MainViewModelFactory()
        mainViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[MainViewModel::class.java]
    }

    //pepao
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}