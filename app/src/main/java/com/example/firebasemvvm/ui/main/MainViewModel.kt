package com.example.firebasemvvm.ui.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasemvvm.data.model.User
import com.example.firebasemvvm.data.repository.UserRepository
import com.example.firebasemvvm.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val authState: StateFlow<Resource<Unit>> = _authState

    fun register(name: String, age: String, email: String, password: String){
        viewModelScope.launch {
            _authState.value = Resource.Loading()
            val result = repository.registerUser(name, age, email, password)
            _authState.value = result
        }
    }

    fun login(email: String, password: String){
        viewModelScope.launch {
            _authState.value = Resource.Loading()
            val result = repository.loginUser(email,password)
            _authState.value = result
        }
    }
}