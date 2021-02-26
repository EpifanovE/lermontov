package ru.eecode.lermontov.repository.db.articles

import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.eecode.lermontov.utils.JsonAssetsLoader
import javax.inject.Inject

class ArticlesSeeder @Inject constructor(
    private val jsonAssetsLoader: JsonAssetsLoader,
) {

    fun seed(db: SupportSQLiteDatabase) {
        val gson = Gson()
        val listType = object : TypeToken<List<Article?>?>() {}.type
        val json: String? = jsonAssetsLoader.load("database/articles.json")
        val articlesList: List<Article> = gson.fromJson(json, listType)

        db.execSQL(getQueryStr(articlesList))
    }

    private fun getQueryStr(articles: List<Article>): String {
        var str = ""
        for (article in articles) {
            str += "(${article.id},'${article.title}','${article.excerpt}','${article.description}','${article.body}'),"
        }

        return "INSERT OR REPLACE INTO articles (id, title, excerpt, description, body) VALUES ${str.trimEnd(',')};"
    }
}