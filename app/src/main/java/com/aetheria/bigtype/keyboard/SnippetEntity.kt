package com.aetheria.bigtype.keyboard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "snippets")
data class SnippetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val body: String,
    val trigger: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class Snippet(
    val id: Long = 0,
    val title: String,
    val body: String,
    val trigger: String
)