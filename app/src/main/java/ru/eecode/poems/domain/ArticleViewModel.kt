package ru.eecode.poems.domain

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.eecode.poems.repository.ArticleRepository
import ru.eecode.poems.repository.db.articles.Article
import ru.eecode.poems.repository.db.favorites.Favorite

class ArticleViewModel  @ViewModelInject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    var articleId: MutableLiveData<Int?> = MutableLiveData(null)

    var article: MutableLiveData<Article?> = MutableLiveData(null)

    lateinit var isFavorite: LiveData<Favorite>

    init {
        articleId.observeForever {
            loadArticle()
            checkIsFavorite()
        }
    }

    private fun loadArticle() {
        viewModelScope.launch {
            var id = articleId.value
            if (id != null) {
                article.value = articleRepository.findArticleById(id)
            }
        }
    }

    private fun checkIsFavorite() {

        if (articleId.value != null) {
            isFavorite = articleRepository.isFavorite(articleId.value!!)
        }
    }

    fun toggleFavorites() {
        if (isFavorite.value != null) {
            removeFromFavorites()
        } else {
            addToFavorites()
        }
    }

    private fun addToFavorites() {
        viewModelScope.launch {
            articleRepository.addToFavorites(articleId.value!!)
        }
    }

    private fun removeFromFavorites() {
        viewModelScope.launch {
            articleRepository.removeFromFavorites(articleId.value!!)
        }
    }
}