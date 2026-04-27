package com.aetheria.bigtype.clipboard

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "clips")
data class ClipEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val type: String = "TEXT"
)

@Dao
interface ClipDao {
    @Query("SELECT * FROM clips ORDER BY isPinned DESC, timestamp DESC LIMIT 50")
    fun getAllClips(): Flow<List<ClipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(clip: ClipEntity)

    @Query("DELETE FROM clips WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE clips SET isPinned = NOT isPinned WHERE id = :id")
    suspend fun togglePin(id: Long)
}