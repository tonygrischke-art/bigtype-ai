package com.aetheria.bigtype.snippets

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "snippets")
data class SnippetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val body: String,
    val trigger: String
)

@Dao
interface SnippetDao {
    @Query("SELECT * FROM snippets WHERE trigger LIKE :query || '%' OR title LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<SnippetEntity>>

    @Query("SELECT * FROM snippets")
    fun getAll(): Flow<List<SnippetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snippet: SnippetEntity)

    @Query("DELETE FROM snippets WHERE id = :id")
    suspend fun delete(id: Long)
}