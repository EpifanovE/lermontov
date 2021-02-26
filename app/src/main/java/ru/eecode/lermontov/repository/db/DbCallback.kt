package ru.eecode.lermontov.repository.db

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.eecode.lermontov.repository.db.articles.ArticlesSeeder
import ru.eecode.lermontov.repository.db.articles.ioThread
import javax.inject.Inject

class DbCallback @Inject constructor(
    private val articlesSeeder: ArticlesSeeder
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        ioThread {
            try {
                articlesSeeder.seed(db)
            } catch (e: Exception) {
                throw e
            }
        }
    }

}