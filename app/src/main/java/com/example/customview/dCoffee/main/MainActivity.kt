package com.example.customview.dCoffee.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.customview.dCoffee.R
import com.example.customview.dCoffee.databinding.ActivityMainBinding
import com.example.customview.dCoffee.detect.DetectedCoffee
import com.example.customview.dCoffee.model.ViewModelFactory
import com.example.customview.dCoffee.preferences.UserPreferences
import com.example.customview.dCoffee.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var mainViewModel : MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvStory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
        setViewModel()
        setAction()
    }

    private fun setView() {
        rvStory = binding.rvListCoffee
        rvStory.setHasFixedSize(true)
    }

    private fun setViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.loading.observe(this) {
            showLoading(it)
        }

        mainViewModel.getToken().observe(this) { session ->
            if (session.Login) {
                mainViewModel.setData(session.token)

            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMenu(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun setAction() {
        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setOnRefreshListener {
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
            swipeRefresh.isRefreshing = false
        }
        binding.ivAddCoffee.setOnClickListener {
            startActivity(Intent(this, DetectedCoffee::class.java))
        }
    }

    private fun setMenu(itemId: Int) {
        when (itemId) {
            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.action_logout -> {
                val builder = AlertDialog.Builder(this)
                val alert = builder.create()
                builder
                    .setTitle(getString(R.string.menuLogout))
                    .setMessage(getString(R.string.alertMassageLogout))
                    .setPositiveButton(getString(R.string.yesLogout)) { _, _ ->
                        mainViewModel.logout()
                    }
                    .setNegativeButton(getString(R.string.cancelLogout)) { _, _ ->
                        alert.cancel()
                    }
                    .show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}