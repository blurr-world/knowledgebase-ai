package com.blurrworld.knowledgebaseai.models

import com.google.gson.annotations.SerializedName

data class RagResponse(
    val citations: List<Citation> = emptyList(),
    val answer: String? = null,
    @SerializedName("is_success")
    val isSuccess: Boolean,
    val message: String? = null
)
