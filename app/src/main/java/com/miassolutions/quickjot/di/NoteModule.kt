package com.miassolutions.quickjot.di

import android.content.Context
import androidx.room.Room
import com.miassolutions.quickjot.data.local.NoteDao
import com.miassolutions.quickjot.data.local.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoteModule {

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): NoteDatabase =
        Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "notes_db"
        ).build()


    @Singleton
    @Provides
    fun provideDao(db: NoteDatabase): NoteDao = db.getNoteDao()

}