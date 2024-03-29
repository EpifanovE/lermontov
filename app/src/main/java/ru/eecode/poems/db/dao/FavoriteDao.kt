package ru.eecode.poems.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import ru.eecode.poems.db.model.Favorite
import ru.eecode.poems.db.model.ArticleListItem

@Dao
interface FavoriteDao {

    @Query("""
        SELECT t2.id as id, title, excerpt, description, 1 as rank, 1 as is_favorite FROM favorites t1
        JOIN articles t2
        ON t1.article_id = t2.id
    """)
    fun getAll() : DataSource.Factory<Int, ArticleListItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorites WHERE article_id = :id")
    fun getById(id: Int): LiveData<Favorite>

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()
}