package com.blurrworld.knowledgebaseai.models

import com.google.gson.annotations.SerializedName

data class Document(
    val id: Int,
    val filename: String,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String
)
