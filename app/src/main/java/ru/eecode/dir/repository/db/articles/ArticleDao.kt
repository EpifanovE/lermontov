package ru.eecode.dir.repository.db.articles

import androidx.paging.DataSource
import androidx.room.*

@Dao
interface ArticleDao {
    @Query("SELECT id, description, title FROM articles ORDER BY title ASC")
    fun getAll(): DataSource.Factory<Int, ArticleListItem>

    @Query("SELECT t1.id, t1.title, snippet(articles_fts) as description FROM articles t1 JOIN articles_fts t2 ON t1.id = t2.rowid WHERE articles_fts MATCH :searchFilter || '*'")
    fun search(searchFilter: String): DataSource.Factory<Int, ArticleListItem>

//    @Query("SELECT t1.id, t1.title, snippet(articles_fts) as description, matchinfo(articles_fts) FROM articles t1 JOIN articles_fts t2 ON t1.id = t2.rowid WHERE articles_fts MATCH :searchFilter || '*'")
//    fun searchWithMatchInfo(searchFilter: String) : DataSource.Factory<Int, ArticleWithMatchinfo>

    @Query("SELECT * FROM articles WHERE id IN (:articlesIds)")
    fun loadAllByIds(articlesIds: IntArray): List<Article>

    @Query("SELECT * FROM articles WHERE id LIKE :id LIMIT 1")
    suspend fun findById(id: Int): Article

    @Delete
    fun delete(article: Article)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<Article>)

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun checkContent(): Int
}