package com.bugsTeam.training.foodrunner.util

import android.widget.EditText

class ValidationUtil {
    companion object{
        fun isValidName(etName: EditText): Boolean = when {
            etName.text.isNullOrEmpty() -> {
                etName.error="field cannot be empty"
                false
            }
            etName.text.toString().length<3 -> {
                etName.error="field required min. 3 character"
                false
            }
            else -> {
                true
            }
        }

        fun isValidEmail(etEmail: EditText): Boolean {
            val emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            return when {

                etEmail.text.isNullOrEmpty() -> {
                    etEmail.error="field cannot be empty"
                    false
                }
                !etEmail.text.toString().matches(emailPattern.toRegex()) -> {
                    etEmail.error="field required min. 3 character"
                    false
                }
                else -> {
                    true
                }
            }
        }

        fun isValidMobile(etMobile: EditText): Boolean = when {
            etMobile.text.isNullOrEmpty() -> {
                etMobile.error="field cannot be empty"
                false
            }
            etMobile.text.toString().length<10 -> {
                etMobile.error="field required min. 10 digits"
                false
            }
            else -> {
                true
            }
        }

        fun isValidAddress(etAddress: EditText): Boolean = when {
            etAddress.text.isNullOrEmpty() -> {
                etAddress.error="field cannot be empty"
                false
            }
            else -> {
                true
            }
        }

        fun isValidPassword(etPassword:EditText,etConfirm:EditText):Boolean=when{
            etPassword.text.isNullOrEmpty()->{
                etPassword.error="field cannot be empty"
                false
            }
            etPassword.text.toString().length<4-> {
                etPassword.error="field required min. 4 characters"
                false
            }
            etPassword.text.equals(etConfirm.text)->{
                etConfirm.error="Both the Password must be same"
                false
            }
            else->{
                true
            }
        }
    }

}