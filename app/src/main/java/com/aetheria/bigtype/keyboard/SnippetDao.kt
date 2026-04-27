package com.aetheria.bigtype.keyboard

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SnippetDao {
    @Query("SELECT * FROM snippets")
    suspend fun getAll(): List<SnippetEntity>
    
    @Query("SELECT * FROM snippets WHERE trigger = :trigger LIMIT 1")
    suspend fun findByTrigger(trigger: String): SnippetEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snippet: SnippetEntity)
    
    @Query("DELETE FROM snippets WHERE id = :id")
    suspend fun delete(id: Long)
    
    @Query("SELECT * FROM snippets WHERE title LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<SnippetEntity>
}