package com.example.firebasemvvm.ui.dashboard

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasemvvm.data.model.User
import com.example.firebasemvvm.data.repository.UserRepository
import com.example.firebasemvvm.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Loading())
    val user: StateFlow<Resource<User>> = _user

    fun loadUser(){
        viewModelScope.launch {
            _user.value = repository.getUserData()
        }
    }

    fun updateUser(name: String, age: String){
        viewModelScope.launch {
            repository.updateUser(name,age)
            loadUser()
        }
    }

    fun uploadPhoto(uri: Uri){
        viewModelScope.launch {
            repository.uploadPhoto(uri)
            loadUser()
        }
    }

    fun deleteAccount(password: String){
        viewModelScope.launch {
            repository.deleteAccount(password)
        }
    }

    fun logout() = repository.logout()

}
