package com.aetheria.bigtype.di

import android.content.Context
import androidx.room.Room
import com.aetheria.bigtype.clipboard.BigTypeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BigTypeDatabase {
        return Room.databaseBuilder(
            context,
            BigTypeDatabase::class.java,
            "bigtype.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideClipDao(db: BigTypeDatabase) = db.clipDao()

    @Provides
    fun provideSnippetDao(db: BigTypeDatabase) = db.snippetDao()
}