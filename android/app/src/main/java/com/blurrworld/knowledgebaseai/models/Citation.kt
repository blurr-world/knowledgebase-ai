package com.blurrworld.knowledgebaseai.models

import com.google.gson.annotations.SerializedName

data class Citation(
    @SerializedName("document_id")
    val documentId: Int,
    @SerializedName("chunk_index")
    val chunkIndex: Int
)
