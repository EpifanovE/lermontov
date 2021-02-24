package ru.eecode.dir.repository.db.articles

import androidx.paging.DataSource
import androidx.room.*

@Dao
interface ArticleDao {
    @Query("""
        SELECT id, description, title, 1 as rank, f1.article_id IS NOT NULL AS is_favorite FROM articles
        LEFT JOIN favorites f1
        ON id = f1.article_id
        ORDER BY title ASC
    """)
    fun getAll(): DataSource.Factory<Int, ArticleListItem>

    @Query("""
        SELECT r1.id, r1.title, r1.description, r1.rank, f1.article_id IS NOT NULL AS is_favorite FROM (
            SELECT t1.id, t1.title,
            t1.description,
            1 AS rank
            FROM articles t1
            JOIN articles_fts
            ON t1.id = articles_fts.rowid
            WHERE articles_fts.title MATCH :searchFilter
            UNION
            SELECT t1.id, t1.title,
            snippet(articles_fts) as description,
            2 AS rank
            FROM articles t1
            JOIN articles_fts
            ON t1.id = articles_fts.rowid
            WHERE articles_fts.body MATCH :searchFilter
        ) r1
        LEFT JOIN favorites f1
        ON r1.id = f1.article_id
        GROUP BY id ORDER BY rank
        """)
    fun search(searchFilter: String) : DataSource.Factory<Int, ArticleListItem>

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