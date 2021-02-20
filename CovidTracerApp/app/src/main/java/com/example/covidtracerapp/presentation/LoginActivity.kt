package com.example.covidtracerapp.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.covidtracerapp.R
import kotlinx.android.synthetic.main.activity_login.loaderLayout
import kotlinx.android.synthetic.main.activity_login.loginBtn
import kotlinx.android.synthetic.main.activity_login.loginField
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel : LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn.setOnClickListener {
            hideKeyboard()
            var id = loginField.editText?.text.toString()
            if (id.isNullOrBlank()) {
                loginField.error = "ID can't be empty"
                loginField.isErrorEnabled = true
            } else {
                loginField.error = null
                viewModel.onLoginClicked(id)
            }
        }

        viewModel.loginState.observe(this, Observer {
            loaderLayout.isVisible = it is Resource.Loading

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
