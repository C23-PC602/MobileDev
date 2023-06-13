package com.example.customview.dcoffee.response

data class StoryListResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<StoryListItem>
)

data class StoryListItem(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: String,
    val lon: String
)

data class FileUploadResponse(
    val error: Boolean,
    val message: String
)