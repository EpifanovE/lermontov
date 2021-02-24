package ru.eecode.dir.domain

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.eecode.dir.repository.ArticleRepository
import ru.eecode.dir.repository.db.articles.Article

class ArticleViewModel  @ViewModelInject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    var articleId: MutableLiveData<Int?> = MutableLiveData(null)

    var article: MutableLiveData<Article?> = MutableLiveData(null)

    init {
        loadArticle()

        articleId.observeForever {
            loadArticle()
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
}