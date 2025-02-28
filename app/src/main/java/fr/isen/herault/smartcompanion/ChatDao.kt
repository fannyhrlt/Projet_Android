package fr.isen.herault.smartcompanion

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(chatMessage: ChatMessage)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): LiveData<List<ChatMessage>>

    @Delete
    suspend fun deleteMessage(chatMessage: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}
