package ru.eecode.dir.repository

import androidx.paging.DataSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.eecode.dir.repository.db.articles.Article
import ru.eecode.dir.repository.db.articles.ArticleDao
import ru.eecode.dir.repository.db.articles.ArticleListItem
import ru.eecode.dir.utils.JsonAssetsLoader
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val articleDao: ArticleDao,
    private val jsonAssetsLoader: JsonAssetsLoader
) {

    fun loadArticles(searchFilter: String?): DataSource.Factory<Int, ArticleListItem> {
        if (searchFilter != null && searchFilter.isNotEmpty()) {
            return articleDao.search(searchFilter)
        }
        return articleDao.getAll()
    }

    suspend fun checkDbContent(): Int {
        return articleDao.checkContent()
    }

    suspend fun articlesSeed() {
        val gson = Gson()
        val listType = object : TypeToken<List<Article?>?>() {}.type
        val json: String? = jsonAssetsLoader.load("database/articles.json")
        val articlesList: List<Article> = gson.fromJson(json, listType)

        articleDao.insertAll(articlesList)
    }

    suspend fun findArticleById(id: Int): Article {
        return articleDao.findById(id)
    }
}