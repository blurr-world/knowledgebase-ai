package com.blurrworld.knowledgebaseai.network

import com.blurrworld.knowledgebaseai.models.DeleteDocumentResponse
import com.blurrworld.knowledgebaseai.models.Document
import com.blurrworld.knowledgebaseai.models.LoginRequest
import com.blurrworld.knowledgebaseai.models.LoginResponse
import com.blurrworld.knowledgebaseai.models.MeResponse
import com.blurrworld.knowledgebaseai.models.RagResponse
import com.blurrworld.knowledgebaseai.models.RegisterRequest
import com.blurrworld.knowledgebaseai.models.RegisterResponse
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/me")
    suspend fun getMe(): Response<MeResponse>

    @POST("/register")
    suspend fun register(
        @Body userData: RegisterRequest
    ): Response<RegisterResponse>

    @POST("/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @GET("/documents")
    suspend fun getAllDocuments(): Response<List<Document>>

    @Multipart
    @POST("/documents")
    suspend fun uploadDocument(
        @Part file: MultipartBody.Part
    ): Response<Document>

    @DELETE("/documents/{document_id}")
    suspend fun deleteDocument(
        @Path("document_id") documentId: Int
    ): Response<DeleteDocumentResponse>


    @POST("/documents/{document_id}/retry")
    suspend fun retryDocument(
        @Path("document_id") documentId: Int
    ): Response<Document>

    @POST("/ask")
    suspend fun askLlm(
        @Query("question") question: String,
        @Query("document_id") documentId: Int
    ): Response<RagResponse>


}
