package com.example.customview.dcoffee.detail_coffee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.customview.dcoffee.databinding.ActivityDetailCoffeeBinding
import com.example.customview.dcoffee.adapter.CoffeeModel

@Suppress("DEPRECATION")
class DetailCoffeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailCoffeeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCoffeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val story = intent.getParcelableExtra<CoffeeModel>("Story") as CoffeeModel
        Glide.with(applicationContext)
            .load(story.photo)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName.text = story.username
        binding.tvDetailDescription.text = story.description
    }
}