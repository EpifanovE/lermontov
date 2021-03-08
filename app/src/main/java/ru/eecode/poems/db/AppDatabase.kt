package ru.eecode.poems.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.eecode.poems.db.dao.ArticleDao
import ru.eecode.poems.db.model.Favorite
import ru.eecode.poems.db.dao.FavoriteDao
import ru.eecode.poems.db.model.Article
import ru.eecode.poems.db.model.ArticleFts

@Database(entities = [Article::class, ArticleFts::class, Favorite::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao

    abstract fun favoriteDao(): FavoriteDao

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