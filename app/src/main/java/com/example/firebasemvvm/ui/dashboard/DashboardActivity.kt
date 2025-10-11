package com.example.firebasemvvm.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasemvvm.databinding.ActivityDashboardBinding
import com.example.firebasemvvm.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar

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

//        lifecycleScope.launchWhenStarted {
//            viewmodel.user.collectLatest {state ->
//                when(state){
//                    is Resource.Loading -> {}
//                    is Resource.Success ->{
//                        val user = state.data ?: return@collectLatest
//                        with(binding){
//                            txtUsername.text = user.name
//                            txtAge.text = "${user.age} y.o"
//                            txtEmail.text = user.email
//                        }
//                        if (user.photoUrl.isNotEmpty()){
//                            Glide.with(this@DashboardActivity).load(user.photoUrl).into(binding.ivProfile)
//                        }
//                    }
//                    is Resource.Error -> Snackbar.make(binding.root,state.message.toString(), Snackbar.LENGTH_SHORT).show()
//                }
//            }
//        }


        binding.btnUploadPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnUpdate.setOnClickListener {
            binding.btnUpdate.isActivated = false

            with(binding){
                etNewName.visibility = View.VISIBLE
                etNewAge.visibility = View.VISIBLE
            }
            val name = binding.etNewName.text.toString()
            val age = binding.etNewAge.text.toString()

            if (name.isNotBlank() && age.isNotBlank()){
                binding.btnUpdate.isActivated = true
                viewmodel.updateUser(name,age)
                Snackbar.make(binding.root,"Profile updated!", Snackbar.LENGTH_SHORT).show()
                with(binding){
                    etNewName.visibility = View.INVISIBLE
                    etNewAge.visibility = View.INVISIBLE
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            viewmodel.logout()
            navigate()
        }

        binding.btnDeleteAccount.setOnClickListener {
            binding.etConfirmPassword.visibility = View.VISIBLE
            val password = binding.etConfirmPassword.text.toString()

            viewmodel.deleteAccount(password)
            Snackbar.make(binding.root,"Account deleted!", Snackbar.LENGTH_SHORT).show()
            navigate()
        }
    }

    private fun navigate(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}