package com.blurrworld.knowledgebaseai

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.blurrworld.knowledgebaseai.databinding.ItemDocumentBinding
import com.blurrworld.knowledgebaseai.models.Document

class DocumentAdapter(
    private var documents: List<Document>,
    private val onDocumentClick: (Document) -> Unit,
    private val onDeleteClick: (Document) -> Unit,
    private val onRetryClick: (Document) -> Unit
) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    fun updateData(newDocuments: List<Document>) {
        documents = newDocuments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val binding = ItemDocumentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DocumentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = documents[position]
        holder.bind(document)
    }

    override fun getItemCount(): Int = documents.size

    inner class DocumentViewHolder(private val binding: ItemDocumentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(document: Document) {
            binding.documentTitle.text = document.filename
            binding.documentStatus.text = document.status.uppercase()

            if (document.status.equals("failed", ignoreCase = true)) {
                binding.documentStatus.setTextColor(Color.parseColor("#E53935")) // Red
            } else if (document.status.equals("ready", ignoreCase = true)) {
                binding.documentStatus.setTextColor(Color.parseColor("#43A047")) // Green
            } else {
                binding.documentStatus.setTextColor(Color.parseColor("#FB8C00")) // Orange
            }

            binding.documentTimestamp.text = document.createdAt

            binding.root.setOnClickListener {
                onDocumentClick(document)
            }

            binding.documentMenuButton.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.menu.add(0, 1, 0, "Delete")
                if (document.status.equals("failed", ignoreCase = true)) {
                    popup.menu.add(0, 2, 1, "Retry")
                }
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        1 -> {
                            onDeleteClick(document)
                            true
                        }
                        2 -> {
                            onRetryClick(document)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }
}
