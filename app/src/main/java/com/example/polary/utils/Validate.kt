package com.example.polary.utils

class Validate {
    companion object {
        fun validateEmail(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun validateOTP(enteredOTP: String, expectedOTP: String, email: String) {
            if (enteredOTP == expectedOTP) {
                // Continue with the sign up process
            } else {
                // Show error message
            }
        }

        fun validateUsername(username: String): Boolean {
            return username.length >= 4
        }

        fun validatePhoneNumber(phoneNumber: String): Boolean {
            return android.util.Patterns.PHONE.matcher(phoneNumber).matches()
        }

        fun validatePassword(password: String, confirmPasswordText: String): Boolean {
            return password == confirmPasswordText
        }

        fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
            return password == confirmPassword
        }

        fun validateSignUp(email: String?, password: String, function: () -> Unit): Boolean {
            // Validate email and password
            // Implement later
            return true
        }
    }
}