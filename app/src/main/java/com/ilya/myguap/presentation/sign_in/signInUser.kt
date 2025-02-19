package com.yourmood.yourmood.presentation.sign_in

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.yourmood.yourmood.presentation.showToast

fun signInUser(
    context: Context,
    auth: FirebaseAuth,
    email: String,
    password: String,
    onResult: (Boolean) -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Вход успешный
                Log.d("Registration", "User signed in successfully")
                onResult(true) // Пользователь успешно вошел
            } else {
                // Вход не удался
                val exception = task.exception
                val message = exception?.message ?: "Unknown error"
                Log.d("Registration", "Sign in failed: $message")
                showToast(context, "Sign in failed: $message")
                onResult(false) // Вход не удался
            }
        }
}