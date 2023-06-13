package com.example.customview.dcoffee.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.customview.dcoffee.R
import com.example.customview.dcoffee.model.ViewModelFactory
import com.example.customview.dcoffee.preferences.UserPreferences
import com.example.customview.dcoffee.databinding.ActivityLoginBinding
import com.example.customview.dcoffee.main.MainActivity
import com.example.customview.dcoffee.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
        loginViewModel.getToken().observe(this){
            if (it.token.isNotEmpty()){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        setActionLogin()
        startAnimation()
    }

    private fun setViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[LoginViewModel::class.java]
        loginViewModel.errorMessage.observe(this) {
            when (it) {
                "success" -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.alertTitle))
                        setPositiveButton(R.string.alertPositive) { _, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
                "onFailure" -> {
                    Toast.makeText(this@LoginActivity, getString(R.string.failureMessage), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Toast.makeText(this@LoginActivity, getString(R.string.emailFormatError), Toast.LENGTH_SHORT).show()
                }
            }
        }
        loginViewModel.loading.observe(this) {
            showLoading(it)
        }
    }

    private fun setActionLogin(){
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            if (password.length < 8){
                Toast.makeText(this,"Password kurang dari 8 karakter", Toast.LENGTH_SHORT).show()
            }
            else{
                loginViewModel.loginUser(email, password)
            }
        }
        binding.actionRegister.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun startAnimation() {
        ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val message = ObjectAnimator.ofFloat(binding.tvMessageSignInPage, View.ALPHA, 1f)
            .setDuration(400)
        val emailEdit = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f)
            .setDuration(400)
        val passwordEdit = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f)
            .setDuration(400)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f)
            .setDuration(400)
        val dontHaveAccount: ObjectAnimator = ObjectAnimator.ofFloat(binding.tvNoAccount, View.ALPHA, 1f)
            .setDuration(400)
        val registerAction = ObjectAnimator.ofFloat(binding.actionRegister, View.ALPHA, 1f)
            .setDuration(400)
        AnimatorSet().apply {
            playSequentially(message, emailEdit, passwordEdit, login, dontHaveAccount, registerAction)
            start()
        }
    }
}