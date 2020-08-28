package com.abdelrahmman.newsapp.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abdelrahmman.newsapp.models.NewsPost

@Database(entities = [NewsPost::class], version = 1)
abstract class AppDatabase: RoomDatabase(){

    abstract fun getNewsPostDao(): NewsPostDao

    companion object{
        const val DATABASE_NAME = "app_db"
    }

}