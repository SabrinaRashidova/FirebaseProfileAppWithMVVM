package com.example.firebasemvvm.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.firebasemvvm.databinding.ActivityDashboardBinding
import com.example.firebasemvvm.ui.main.MainActivity
import com.example.firebasemvvm.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
                    is Resource.Loading -> {Snackbar.make(binding.root,"Loading...", Snackbar.LENGTH_SHORT).show()}
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

//        lifecycleScope.launch {
//            viewmodel.user.collectLatest { state ->
//                when (state) {
//                    is Resource.Loading -> Unit
//                    is Resource.Success -> {
//                        val user = state.data ?: return@collectLatest
//                        binding.apply {
//                            txtUsername.text = user.name
//                            txtAge.text = "${user.age} y.o"
//                            txtEmail.text = user.email
//                        }
//                        if (user.photoUrl.isNotEmpty()) {
//                            Glide.with(this@DashboardActivity)
//                                .load(user.photoUrl)
//                                .into(binding.ivProfile)
//                        }
//                    }
//                    is Resource.Error -> {
//                        Snackbar.make(binding.root, state.message ?: "Error", Snackbar.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }


        binding.btnUploadPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        var isEditing = false

        binding.btnUpdate.setOnClickListener {
            if (!isEditing) {
                isEditing = true
                binding.etNewName.visibility = View.VISIBLE
                binding.etNewAge.visibility = View.VISIBLE
                binding.btnUpdate.text = "Save"
            } else {
                val name = binding.etNewName.text.toString().trim()
                val age = binding.etNewAge.text.toString().trim()

                if (name.isBlank() || age.isBlank()) {
                    Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                binding.btnUpdate.isClickable = false

                viewmodel.updateUser(name, age)

                Snackbar.make(binding.root, "Profile updated!", Snackbar.LENGTH_SHORT).show()

                binding.etNewName.visibility = View.GONE
                binding.etNewAge.visibility = View.GONE
                binding.btnUpdate.text = "Update Profile"
                isEditing = false
                binding.btnUpdate.isClickable = true
            }
        }


        binding.btnLogout.setOnClickListener {
            viewmodel.logout()
            Snackbar.make(binding.root, "Logged out successfully!", Snackbar.LENGTH_SHORT).show()

            binding.root.postDelayed({
                navigate()
            }, 400)
        }


        binding.btnDeleteAccount.setOnClickListener {
            binding.etConfirmPassword.visibility = View.VISIBLE
            val password = binding.etConfirmPassword.text.toString()

            lifecycleScope.launchWhenStarted {
                viewmodel.deleteState.collectLatest { state ->
                    when (state) {
                        is Resource.Loading -> {
                            Snackbar.make(
                                binding.root,
                                "Deleting account...",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Success -> {
                            Snackbar.make(binding.root, "Account deleted!", Snackbar.LENGTH_SHORT)
                                .show()
                            navigate()
                        }

                        is Resource.Error -> {
                            Snackbar.make(
                                binding.root,
                                state.message.toString(),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }else -> Snackbar.make(binding.root,"Unknown error", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }

            viewmodel.deleteAccount(password)
        }


    }

    private fun navigate(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}