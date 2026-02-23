package com.real.vibechat.di

import android.content.Context
import androidx.room.Room
import com.real.vibechat.data.room.AppDatabase
import com.real.vibechat.data.room.ChatDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "vibe_chat_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideChatDao(
        database: AppDatabase
    ): ChatDAO {
        return database.chatDAO()
    }
}