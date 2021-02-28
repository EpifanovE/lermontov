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

            if (it != null) {
                checkIsFavorite(it)
            }
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

    private fun checkIsFavorite(id: Int) {
        isFavorite = articleRepository.isFavorite(id)
    }

    fun toggleFavorites() {
        if (isFavorite.value != null) {
            removeFromFavorites(articleId.value)
        } else {
            addToFavorites(articleId.value)
        }
    }

    private fun addToFavorites(id: Int?) {
        if (id == null) return
        viewModelScope.launch {
            articleRepository.addToFavorites(id)
        }
    }

    private fun removeFromFavorites(id: Int?) {
        if (id == null) return
        viewModelScope.launch {
            articleRepository.removeFromFavorites(id)
        }
    }
}