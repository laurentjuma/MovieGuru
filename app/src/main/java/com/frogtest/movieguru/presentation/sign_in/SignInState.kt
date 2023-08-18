package com.frogtest.movieguru.presentation.sign_in

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val isEmailVerificationSent: Boolean = false,
    val isEmailVerified: Boolean = false,
    val message: String? = null
)
