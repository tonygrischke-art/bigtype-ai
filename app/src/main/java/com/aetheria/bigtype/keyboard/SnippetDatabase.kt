// DEPRECATED: Use com.aetheria.bigtype.clipboard.BigTypeDatabase instead.
package com.aetheria.bigtype.keyboard

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SnippetEntity::class], version = 1, exportSchema = false)
abstract class SnippetDatabase : RoomDatabase() {
    abstract fun snippetDao(): SnippetDao
}