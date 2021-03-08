package ru.eecode.poems.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.eecode.poems.repository.db.articles.Article
import ru.eecode.poems.repository.db.articles.ArticleDao
import ru.eecode.poems.repository.db.articles.ArticleListItem
import ru.eecode.poems.repository.db.favorites.Favorite
import ru.eecode.poems.repository.db.favorites.FavoriteDao
import ru.eecode.poems.utils.JsonAssetsLoader
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val articleDao: ArticleDao,
    private val favoriteDao: FavoriteDao,
    private val jsonAssetsLoader: JsonAssetsLoader
) {
    fun loadArticles(searchFilter: String?): DataSource.Factory<Int, ArticleListItem> {
        if (searchFilter != null && searchFilter.isNotEmpty()) {
            return articleDao.search(sanitizeSearchQuery(searchFilter))
        }
        return articleDao.getAll()
    }

    suspend fun checkDbContent(): Int {
        return articleDao.checkContent()
    }

    suspend fun articlesSeed() {
        val gson = Gson()
        val listType = object : TypeToken<List<Article>>() {}.type
        val json: String? = jsonAssetsLoader.load("database/articles.json")
        val articlesList: List<Article> = gson.fromJson(json, listType)

        articleDao.insertAll(articlesList)
    }

    suspend fun findArticleById(id: Int): Article {
        return articleDao.findById(id)
    }

    private fun sanitizeSearchQuery(query: String?): String {
        if (query == null) {
            return "";
        }
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "#\"$queryWithEscapedQuotes*\"#"
    }

    fun loadFavorites(): DataSource.Factory<Int, ArticleListItem> {
        return favoriteDao.getAll()
    }

    suspend fun addToFavorites(id: Int) {
        favoriteDao.addFavorite(Favorite(id))
    }

    suspend fun removeFromFavorites(id: Int) {
        favoriteDao.deleteFavorite(Favorite(id));
    }

    fun isFavorite(id: Int) : LiveData<Favorite> {
        return favoriteDao.getById(id)
    }

    suspend fun clearFavorites() {
        favoriteDao.deleteAll()
    }
}