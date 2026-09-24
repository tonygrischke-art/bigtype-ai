package com.aetheria.bigtype.clipboard

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aetheria.bigtype.keyboard.SnippetDao
import com.aetheria.bigtype.keyboard.SnippetEntity
import com.aetheria.bigtype.keyboard.AutocorrectDao
import com.aetheria.bigtype.keyboard.AutocorrectRuleEntity
import com.aetheria.bigtype.keyboard.EmojiPredictionDao
import com.aetheria.bigtype.keyboard.EmojiPredictionEntity

@Database(
    entities = [
        ClipEntity::class,
        SnippetEntity::class,
        AutocorrectRuleEntity::class,
        EmojiPredictionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BigTypeDatabase : RoomDatabase() {
    abstract fun clipDao(): ClipDao
    abstract fun snippetDao(): SnippetDao
    abstract fun autocorrectDao(): AutocorrectDao
    abstract fun emojiPredictionDao(): EmojiPredictionDao
}