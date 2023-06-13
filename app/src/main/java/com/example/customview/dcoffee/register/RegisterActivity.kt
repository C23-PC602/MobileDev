package com.example.customview.dcoffee.register

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
import com.example.customview.dcoffee.response.RegisterResult
import com.example.customview.dcoffee.databinding.ActivityRegisterBinding
import com.example.customview.dcoffee.login.LoginActivity
import com.example.customview.dcoffee.model.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()
        setActionRegister()
        playAnimation()
    }

    private fun setViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[RegisterViewModel::class.java]
        registerViewModel.errorMessage.observe(this) {
            when (it) {
                "User created" -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.alertTitle))
                        setMessage(getString(R.string.alertCreateAccount))
                        setPositiveButton(getString(R.string.alertPositive)) { _, _ ->
                            val intent = Intent(this@RegisterActivity,
                                LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
                "onFailure" -> {
                    Toast.makeText(this@RegisterActivity, getString(R.string.failureMessage), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.edRegisterEmail.error = getString(R.string.emailFormatError)
                }
            }
        }
        registerViewModel.loading.observe(this) {
            showLoading(it)
        }
    }

    private fun setActionRegister(){
        binding.actionIn.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            registerViewModel.registerUser(RegisterResult(name, email, password))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.apply {
            visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val title = ObjectAnimator.ofFloat(binding.titleRegister, View.ALPHA, 1f)
            .setDuration(600)
        val nameEdit = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f)
            .setDuration(600)
        val emailEdit = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f)
            .setDuration(600)
        val passwordEdit = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f)
            .setDuration(600)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f)
            .setDuration(600)
        val alreadyHaveAnAccount: ObjectAnimator = ObjectAnimator.ofFloat(binding.tvHaveAccount, View.ALPHA, 1f)
            .setDuration(600)
        val loginAction =ObjectAnimator.ofFloat(binding.actionIn, View.ALPHA, 1f)
            .setDuration(600)
        AnimatorSet().apply {
            playSequentially(title, nameEdit, emailEdit, passwordEdit, register, alreadyHaveAnAccount,
                loginAction)
            start()
        }
    }
}