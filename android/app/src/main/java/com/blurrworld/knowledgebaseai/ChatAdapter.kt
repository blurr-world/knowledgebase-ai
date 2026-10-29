package com.blurrworld.knowledgebaseai

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.blurrworld.knowledgebaseai.databinding.ItemChatMessageBinding

data class ChatMessage(val text: String, val isUser: Boolean)

class ChatAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.binding.messageText.text = message.text

        val rootLayout = holder.binding.root as LinearLayout
        val context = holder.itemView.context

        val typedValuePrimary = android.util.TypedValue()
        val typedValueOnPrimary = android.util.TypedValue()
        val typedValueSurfaceVariant = android.util.TypedValue()
        val typedValueOnSurfaceVariant = android.util.TypedValue()

        if (message.isUser) {
            rootLayout.gravity = Gravity.END
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorPrimaryContainer, typedValuePrimary, true)
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimaryContainer, typedValueOnPrimary, true)
            holder.binding.messageCard.setCardBackgroundColor(typedValuePrimary.data)
            holder.binding.messageText.setTextColor(typedValueOnPrimary.data)
        } else {
            rootLayout.gravity = Gravity.START
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorSurfaceVariant, typedValueSurfaceVariant, true)
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurfaceVariant, typedValueOnSurfaceVariant, true)
            holder.binding.messageCard.setCardBackgroundColor(typedValueSurfaceVariant.data)
            holder.binding.messageText.setTextColor(typedValueOnSurfaceVariant.data)
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
