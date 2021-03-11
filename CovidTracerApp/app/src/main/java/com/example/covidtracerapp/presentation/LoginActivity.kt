package com.example.covidtracerapp.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.covidtracerapp.R
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel : LoginViewModel by viewModel()
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn.setOnClickListener {
            hideKeyboard()
            var id = loginField.editText?.text.toString()
            var password = passwordField.editText?.text.toString()

            if (id.isNullOrBlank()) {
                loginField.error = "ID can't be empty"
                loginField.isErrorEnabled = true
            }

            if (password.isNullOrBlank()) {
                passwordField.error = "Password can't be empty"
                passwordField.isErrorEnabled = true
            }

            if (id.isNotBlank() && password.isNotBlank()) {
                loginField.error = null
                passwordField.error = null
//                viewModel.onLoginClicked(id)
                viewModel.getToken(id, password)
            }

            //TODO: Login Skipped for debugging
//            viewModel.getToken("010101000006", "TestPassword")
        }

        viewModel.tokenState.observe(this, Observer {
            if (it is Resource.Error) setErrorVisible(true)
        })

        viewModel.loginState.observe(this, Observer {
            loaderLayout.isVisible = it is Resource.Loading
            setErrorVisible(false)

            when (it) {
                is Resource.Success -> {
                    val intent = Intent(this, ShowBeaconsActivity::class.java)
                    intent.putExtra("USER", it.data)
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> showError(it.message)

            }
        })
    }
    private fun setErrorVisible(boolean: Boolean){
        tvIncorrectCredentials.isVisible = boolean
        ivIncorrectCredentials.isVisible = boolean
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
