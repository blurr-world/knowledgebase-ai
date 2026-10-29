package com.blurrworld.knowledgebaseai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blurrworld.knowledgebaseai.auth.AuthActivity
import com.blurrworld.knowledgebaseai.auth.TokenManager
import com.blurrworld.knowledgebaseai.databinding.FragmentDocumentBinding
import com.blurrworld.knowledgebaseai.models.Document
import com.blurrworld.knowledgebaseai.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class DocumentFragment : Fragment() {

    private var _binding: FragmentDocumentBinding? = null
    private val binding get() = _binding!!
    private lateinit var documentAdapter: DocumentAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadDocumentFile(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDocumentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        documentAdapter = DocumentAdapter(
            emptyList(),
            onDocumentClick = { document ->
                sharedViewModel.selectDocument(document)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            },
            onDeleteClick = { document ->
                deleteDocument(document.id)
            },
            onRetryClick = { document ->
                retryDocument(document.id)
            }
        )

        binding.documentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.documentsRecyclerView.adapter = documentAdapter

        binding.uploadFab.setOnClickListener {
            filePickerLauncher.launch("*/*")
        }

        binding.signOutButton.setOnClickListener {
            val tokenManager = TokenManager(requireContext())
            tokenManager.clearToken()
            ApiClient.tokenProvider = { null }
            val intent = Intent(requireContext(), AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }

        loadAccountAndDocuments()
    }

    private fun uploadDocumentFile(uri: Uri) {
        val contentResolver = requireContext().contentResolver
        val fileName = getFileName(uri) ?: "document.pdf"

        Toast.makeText(requireContext(), "Uploading $fileName...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return@launch
                inputStream.close()

                val requestBody = bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", fileName, requestBody)

                val response = ApiClient.api.uploadDocument(multipartBody)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Document uploaded successfully", Toast.LENGTH_SHORT).show()
                        loadAccountAndDocuments()
                    } else {
                        Toast.makeText(requireContext(), "Upload failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
        return name
    }

    private fun loadAccountAndDocuments() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch Account Info (/me)
                val meResponse = ApiClient.api.getMe()
                // Fetch Documents (/documents)
                val docsResponse = ApiClient.api.getAllDocuments()

                withContext(Dispatchers.Main) {
                    if (meResponse.isSuccessful && meResponse.body() != null) {
                        val user = meResponse.body()!!
                        binding.userIdText.text = "User ID: ${user.id}"
                        binding.userEmailText.text = user.email
                        binding.documentCountText.text = "${user.totalDocuments} documents"
                    }

                    if (docsResponse.isSuccessful && docsResponse.body() != null) {
                        val docs = docsResponse.body()!!
                        documentAdapter.updateData(docs)
                    } else {
                        Toast.makeText(requireContext(), "Failed to load documents", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteDocument(documentId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.deleteDocument(documentId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Document deleted", Toast.LENGTH_SHORT).show()
                        if (sharedViewModel.selectedDocument.value?.id == documentId) {
                            sharedViewModel.selectDocument(null)
                        }
                        loadAccountAndDocuments()
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete document", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun retryDocument(documentId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.api.retryDocument(documentId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Document retry initiated", Toast.LENGTH_SHORT).show()
                        loadAccountAndDocuments()
                    } else {
                        Toast.makeText(requireContext(), "Failed to retry document", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
