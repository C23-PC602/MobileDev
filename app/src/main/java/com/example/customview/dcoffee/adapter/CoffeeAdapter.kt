package com.example.customview.dcoffee.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.customview.dcoffee.databinding.ListItemCoffeeBinding
import com.example.customview.dcoffee.model.CoffeeModel
import com.example.customview.dcoffee.detail_coffee.DetailCoffeeActivity

class CoffeeAdapter(private val listStory: ArrayList<CoffeeModel>) : RecyclerView.Adapter<CoffeeAdapter.ListViewHolder>() {
    inner class ListViewHolder(binding: ListItemCoffeeBinding) : RecyclerView.ViewHolder(binding.root) {
        private var photo = binding.ivItemPhoto
        private var userName = binding.tvItemName

        fun bind(story: CoffeeModel) {
            Glide.with(itemView.context)
                .load(story.photo)
                .into(photo)
            userName.text = story.username
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailCoffeeActivity::class.java)
                intent.putExtra("Story", story)
                itemView.context.startActivity(intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity)
                        .toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemBinding = ListItemCoffeeBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size
}