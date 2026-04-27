package com.aetheria.bigtype.clipboard

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ClipEntity::class], version = 1, exportSchema = false)
abstract class ClipboardDatabase : RoomDatabase() {
    abstract fun clipDao(): ClipDao
}
