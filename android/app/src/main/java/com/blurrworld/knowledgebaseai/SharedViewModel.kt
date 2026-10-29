package com.blurrworld.knowledgebaseai

import androidx.lifecycle.ViewModel
import com.blurrworld.knowledgebaseai.models.Document
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel : ViewModel() {
    private val _selectedDocument = MutableStateFlow<Document?>(null)
    val selectedDocument: StateFlow<Document?> = _selectedDocument

    fun selectDocument(document: Document?) {
        _selectedDocument.value = document
    }
}
