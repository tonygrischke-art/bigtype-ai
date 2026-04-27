package com.aetheria.bigtype.clipboard

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aetheria.bigtype.snippets.SnippetDao
import com.aetheria.bigtype.snippets.SnippetEntity

@Database(
    entities = [ClipEntity::class, SnippetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BigTypeDatabase : RoomDatabase() {
    abstract fun clipDao(): ClipDao
    abstract fun snippetDao(): SnippetDao
}