package com.example.firebasemvvm.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.firebasemvvm.databinding.ActivityMainBinding
import com.example.firebasemvvm.ui.dashboard.DashboardActivity
import com.example.firebasemvvm.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewmodel: MainViewModel by viewModels { MainViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        lifecycleScope.launchWhenStarted {
//            viewmodel.authState.collectLatest { state ->
//                when (state){
//                    is Resource.Loading -> Snackbar.make(binding.root,"Please wait....", Snackbar.LENGTH_SHORT).show()
//                    is Resource.Success -> navigate()
//                    is Resource.Error -> Snackbar.make(binding.root,state.message.toString(),
//                        Snackbar.LENGTH_SHORT).show()
//                }
//            }
//        }

        binding.btnRegister.setOnClickListener {
            with(binding){
                etName.visibility = View.VISIBLE
                etAge.visibility = View.VISIBLE
                etEmail.visibility = View.VISIBLE
                etPassword.visibility = View.VISIBLE
                btnLogin.visibility = View.INVISIBLE
            }

            val name = binding.etName.text.toString()
            val age = binding.etAge.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (name.isBlank() || age.isBlank() || email.isBlank() || password.isBlank()){
                Snackbar.make(binding.root,"Please fill all the fields", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Snackbar.make(binding.root,"Invalid email", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6){
                Snackbar.make(binding.root,"Password must be at least 6 characters", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewmodel.register(name,age,email,password)
            Snackbar.make(binding.root,"Registered Successfully", Snackbar.LENGTH_SHORT).show()
            navigate()
        }

        binding.btnLogin.setOnClickListener {

            binding.etEmail.visibility = View.VISIBLE
            binding.etPassword.visibility = View.VISIBLE
            binding.btnRegister.visibility = View.INVISIBLE

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isNotBlank() && password.isNotBlank()){
                viewmodel.login(email, password)
                Snackbar.make(binding.root,"Logged in Successfully", Snackbar.LENGTH_SHORT).show()
                navigate()
            }else{
                Snackbar.make(binding.root,"Please fill all the fields", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    private fun navigate(){
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}