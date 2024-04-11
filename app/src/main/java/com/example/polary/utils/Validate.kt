package com.example.polary.utils


import com.example.polary.Class.HttpMethod
import com.example.polary.utils.ApiCallBack
import kotlinx.coroutines.CompletableDeferred

data class OTPRequestBody(val email: String, val otp: String)

class Validate {
    companion object {
        fun validateEmail(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        suspend fun validateOTP(email: String, enteredOTP: String): Boolean {
            val httpMethod = HttpMethod()
            val requestBody = OTPRequestBody(email, enteredOTP)
            val result = CompletableDeferred<Boolean>()

            httpMethod.doPost("auth/verify-otp", requestBody, object : ApiCallBack<Any> {
                override fun onSuccess(data: Any) {
                    // Assuming the API returns a success status in the data when the OTP is valid
                    result.complete(true)
                }

                override fun onError(error: Throwable) {
                    // Log the error and complete the result with false
                    result.complete(false)
                }
            })

            return result.await()
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