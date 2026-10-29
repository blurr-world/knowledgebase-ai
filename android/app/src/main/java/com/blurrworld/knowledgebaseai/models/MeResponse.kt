package com.blurrworld.knowledgebaseai.models

import com.google.gson.annotations.SerializedName

data class MeResponse(
    val id: Int,
    val email: String,
    @SerializedName("total_documents")
    val totalDocuments: Int
)
