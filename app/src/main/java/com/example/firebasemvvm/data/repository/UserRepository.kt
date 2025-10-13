package com.example.firebasemvvm.data.repository

import android.net.Uri
import android.util.Log
import com.example.firebasemvvm.data.model.User
import com.example.firebasemvvm.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class UserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    suspend fun registerUser(name: String, age: String, email: String, password: String): Resource<Unit>{
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return Resource.Error("User not found")
            val user = User(name,age,email,"")
            Log.d("Firebase", "Registering user with ID: $userId")

            firestore.collection("users").document(userId).set(user).await()
            Log.d("Firebase", "User saved in Firestore successfully!")
            Resource.Success(Unit)
        }catch (e: Exception){
            Log.e("Firebase", "Registration failed: ${e.message}", e)
            Resource.Error(e.message?: "Registration failed")
        }
    }

    suspend fun loginUser(email: String, password: String) : Resource<Unit>{
        return try {
            auth.signInWithEmailAndPassword(email,password).await()
            Resource.Success(Unit)
        }catch (e:Exception){
            Resource.Error(e.message ?: "Login failed")
        }
    }

    suspend fun getUserData(): Resource<User>{
        return try {
            val userId= auth.currentUser?.uid ?: return Resource.Error("User not logged in")
            val snapshot = firestore.collection("users").document(userId).get().await()
            val user = snapshot.toObject(User::class.java)
            Resource.Success(user ?: User())
        }catch (e: Exception){
            Resource.Error(e.message ?: "Failed to load the user data")
        }
    }

    suspend fun updateUser(name: String, age: String) : Resource<Unit>{
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not logged in")
            val updates = mapOf("name" to name, "age" to age)
            firestore.collection("users").document(userId).update(updates).await()
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message ?: "Update failed")
        }
    }

    suspend fun uploadPhoto(uri: Uri): Resource<String>{
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not logged in")
            val ref = storage.reference.child("profile_photos/$userId.jpg")

            ref.putFile(uri).await()
            val downloadUrl= ref.downloadUrl.await()

            firestore.collection("users").document(userId).update("photoUrl",downloadUrl.toString()).await()
            Resource.Success(downloadUrl.toString())
        }catch (e: kotlin.Exception){
            Resource.Error(e.message ?: "Upload failed")
        }
    }

    suspend fun deleteAccount(password: String) : Resource<Unit>{
        return try {
            val user = auth.currentUser ?: return Resource.Error("User not logged in")
            val email = user.email ?: return Resource.Error("Email missing")

            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email,password)
            user.reauthenticate(credential).await()
            firestore.collection("users").document(user.uid).delete().await()
            user.delete().await()
            auth.signOut()

            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message ?: "Account deletion failed")
        }
    }

    fun logout(){
        auth.signOut()
    }
}