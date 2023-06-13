package com.example.customview.dcoffee.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.customview.dcoffee.R
import com.example.customview.dcoffee.adapter.CoffeeModel
import com.example.customview.dcoffee.model.ViewModelFactory
import com.example.customview.dcoffee.preferences.UserPreferences
import com.example.customview.dcoffee.adapter.CoffeeAdapter
import com.example.customview.dcoffee.databinding.ActivityMainBinding
import com.example.customview.dcoffee.detect.DetectedCoffee
import com.example.customview.dcoffee.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var mainViewModel : MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var rvCoffee: RecyclerView
    private val list = ArrayList<CoffeeModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
        setViewModel()
        setAction()
    }

    private fun setView() {
        rvCoffee = binding.rvListStory
        rvCoffee.setHasFixedSize(true)
    }

    private fun setViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.listStory.observe(this) {
            list.clear()
            for (coffee in it) {
                list.add(
                    CoffeeModel(
                        coffee.id,
                        coffee.name,
                        coffee.description,
                        coffee.photoUrl,
                        coffee.createdAt
                    )
                )
            }
            rvCoffee.layoutManager = LinearLayoutManager(this)

            val listCoffeeAdapter = CoffeeAdapter(list)
            rvCoffee.adapter = listCoffeeAdapter
        }

        mainViewModel.loading.observe(this) {
            showLoading(it)
        }

        mainViewModel.errorMessage.observe(this) {
            when (it) {
                "Story Loaded Successfully" -> {
                    Toast.makeText(this@MainActivity, getString(R.string.coffeeLoadedSuccess), Toast.LENGTH_SHORT).show()
                }
                "onFailure" -> {
                    Toast.makeText(this@MainActivity, getString(R.string.failureMessage), Toast.LENGTH_SHORT).show()
                }
            }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMenu(item.itemId)
        return super.onOptionsItemSelected(item)
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