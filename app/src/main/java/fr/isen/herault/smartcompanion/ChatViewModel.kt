package fr.isen.herault.smartcompanion

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val chatDao: ChatDao = AppDatabase.getDatabase(application).chatDao()
    val allMessages: LiveData<List<ChatMessage>> = chatDao.getAllMessages()

    fun insertMessage(question: String, answer: String) {
        viewModelScope.launch {
            chatDao.insertMessage(ChatMessage(question = question, answer = answer))
        }
    }

    fun deleteMessage(chatMessage: ChatMessage) {
        viewModelScope.launch {
            chatDao.deleteMessage(chatMessage)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            chatDao.clearHistory()
        }
    }
}
