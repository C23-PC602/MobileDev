package com.example.customview.dcoffee.api

import com.example.customview.dcoffee.response.LoginResponse
import com.example.customview.dcoffee.response.RegisterResponse
import com.example.customview.dcoffee.response.FileUploadResponse
import com.example.customview.dcoffee.response.StoryListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addStories(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") authHeader: String
    ): Call<FileUploadResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String
    ): Call<StoryListResponse>
}