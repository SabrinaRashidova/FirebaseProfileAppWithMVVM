package com.example.firebasemvvm.ui.dashboard

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.firebasemvvm.databinding.ActivityDashboardBinding
import com.example.firebasemvvm.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewmodel: DashboardViewModel by viewModels { DashboardViewModelFactory() }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){uri ->
        if (uri != null){
            viewmodel.uploadPhoto(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewmodel.loadUser()

        lifecycleScope.launchWhenStarted {
            viewmodel.user.collectLatest {state ->
                when(state){
                    is Resource.Loading -> {}
                    is Resource.Success ->{
                        val user = state.data ?: return@collectLatest
                        with(binding){
                            txtUsername.text = user.name
                            txtAge.text = "${user.age} y.o"
                            txtEmail.text = user.email
                        }
                        if (user.photoUrl.isNotEmpty()){
                            Glide.with(this@DashboardActivity).load(user.photoUrl).into(binding.ivProfile)
                        }
                    }
                    is Resource.Error -> Snackbar.make(binding.root,state.message.toString(), Snackbar.LENGTH_SHORT).show()
                }
            }
        }


        binding.btnUploadPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }



    }
}