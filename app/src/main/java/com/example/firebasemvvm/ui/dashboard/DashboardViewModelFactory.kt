package com.example.firebasemvvm.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firebasemvvm.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DashboardViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = UserRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
            FirebaseStorage.getInstance())
        return DashboardViewModel(repo) as T
    }
}