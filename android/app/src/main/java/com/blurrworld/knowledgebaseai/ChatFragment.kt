package com.blurrworld.knowledgebaseai

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blurrworld.knowledgebaseai.databinding.FragmentChatBinding
import com.blurrworld.knowledgebaseai.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var chatAdapter: ChatAdapter
    private val messageList = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatAdapter = ChatAdapter(messageList)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.chatRecyclerView.adapter = chatAdapter

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.nav_document -> {
                    (requireActivity() as MainActivity).changeFragment(DocumentFragment())
                    true
                }
                else -> false
            }
        }

        val navigateToDocs = View.OnClickListener {
            (requireActivity() as MainActivity).changeFragment(DocumentFragment())
        }
        binding.selectedDocumentCard.setOnClickListener(navigateToDocs)
        binding.changeDocButton.setOnClickListener(navigateToDocs)
        binding.suggestionExplain.setOnClickListener(navigateToDocs)

        var previousDocumentId: Int? = null
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.selectedDocument.collectLatest { document ->
                if (document != null) {
                    binding.selectedDocTitle.text = document.filename
                    binding.selectedDocTimestamp.text = "Uploaded: ${document.createdAt}"
                    binding.changeDocButton.text = "Change"
                    binding.emptyChatView.visibility = View.GONE
                    binding.chatRecyclerView.visibility = View.VISIBLE

                    if (previousDocumentId != document.id) {
                        messageList.clear()
                        chatAdapter.notifyDataSetChanged()
                        previousDocumentId = document.id
                    }
                } else {
                    binding.selectedDocTitle.text = "No document selected"
                    binding.selectedDocTimestamp.text = "Tap here to select a document"
                    binding.changeDocButton.text = "Select"
                    binding.emptyChatView.visibility = View.VISIBLE
                    binding.chatRecyclerView.visibility = View.GONE

                    messageList.clear()
                    chatAdapter.notifyDataSetChanged()
                    previousDocumentId = null
                }
            }
        }

        binding.sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val question = binding.messageInput.text?.toString()?.trim() ?: ""
        if (question.isEmpty()) return

        val currentDoc = sharedViewModel.selectedDocument.value
        if (currentDoc == null) {
            Toast.makeText(requireContext(), "Please select a document first", Toast.LENGTH_SHORT).show()
            return
        }

        // Add user message
        messageList.add(ChatMessage(question, true))
        chatAdapter.notifyItemInserted(messageList.size - 1)
        binding.chatRecyclerView.scrollToPosition(messageList.size - 1)
        binding.messageInput.setText("")

        // Call RAG API
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiClient.api.askLlm(question, currentDoc.id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val ragResponse = response.body()!!
                        val answer = ragResponse.answer ?: ragResponse.message ?: "No answer received"
                        messageList.add(ChatMessage(answer, false))
                        chatAdapter.notifyItemInserted(messageList.size - 1)
                        binding.chatRecyclerView.scrollToPosition(messageList.size - 1)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Log.d("TAG", "sendMessage: ${errorBody}")
                        Toast.makeText(requireContext(), "Error (${response.code()}): $errorBody", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
