package fr.isen.herault.smartcompanion

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface InteractionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: Interaction)

    @Query("SELECT * FROM interactions ORDER BY timestamp DESC")
    suspend fun getAllInteractions(): List<Interaction>

    @Query("DELETE FROM interactions WHERE id = :interactionId")
    suspend fun deleteInteraction(interactionId: Int)

    @Query("DELETE FROM interactions")
    suspend fun deleteAllInteractions()
}