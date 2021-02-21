package ru.eecode.dir.repository.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.eecode.dir.repository.db.articles.*

@Database(entities = [Article::class, ArticleFts::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabaseClient(
            context: Context
        ): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(
            context: Context
        ): AppDatabase =
            Room
                .databaseBuilder(context.applicationContext, AppDatabase::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .build()

    }
}