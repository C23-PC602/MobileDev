package com.example.customview.dcoffee.adapter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class CoffeeModel(
    val storyId: String,
    val username: String,
    val description: String,
    val photo: String,
    val created: String,
): Parcelable
