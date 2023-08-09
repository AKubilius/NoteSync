package com.android.notesync

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.android.notesync.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    private val preferences: NotesPreferences by lazy {
        NotesPreferences(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (preferences.isLoggedIn()) {
            startMainActivity()
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener { onLoginClicked() }

        binding.textUsernameLayout.editText
            ?.addTextChangedListener(createTextWatcher(binding.textUsernameLayout))

        binding.textPasswordInput.editText
            ?.addTextChangedListener(createTextWatcher(binding.textPasswordInput))

    }
    private fun createTextWatcher(textPasswordInput: TextInputLayout): TextWatcher? {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence,
                                           start: Int, count: Int, after: Int) {
                // not needed
            }

            override fun onTextChanged(s: CharSequence,
                                       start: Int, before: Int, count: Int) {
                binding.textPasswordInput.error = null
            }

            override fun afterTextChanged(s: Editable) {
                // not needed
            }
        }
    }
    private fun onLoginClicked() {
        val username: String = binding.textUsernameLayout.editText?.text.toString()
        val password: String = binding.textPasswordInput.editText?.text.toString()
        Log.w("NAME", username)
        Log.w("PASSWORD", password)
        if (username.isEmpty()) {
            binding.textUsernameLayout.error = "Username must not be empty"
        } else if (password.isEmpty()) {
            binding.textPasswordInput.error = "Password must not be empty"
        }else if (username != "admin" && password != "admin") {
            showErrorDialog()
        } else {
            performLogin()
        }
    }
    private fun showErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("Login Failed")
            .setMessage("Username or password is not correct. Please try again.")
            .setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
            .show()
    }
    private fun performLogin() {
        preferences.setLoggedIn(true)
        binding.textUsernameLayout.isEnabled = false
        binding.textPasswordInput.isEnabled = false
        binding.loginButton.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            startMainActivity()
            finish()
        }, 2000)
    }
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}