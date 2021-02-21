package ru.eecode.dir.domain

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.eecode.dir.repository.ArticleRepository
import ru.eecode.dir.repository.db.articles.Article
import ru.eecode.dir.repository.db.articles.ArticleListItem

class ArticleIndexViewModel @ViewModelInject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    var articles: LiveData<PagedList<ArticleListItem>>

    var filter: MutableLiveData<String?> = MutableLiveData(null)

    var destroyed: MutableLiveData<Boolean> = MutableLiveData(false)

    var article: MutableLiveData<Article?> = MutableLiveData(null)

    init {
        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .build()

        articles = Transformations.switchMap(filter) {
            articleRepository.loadArticles(it).toLiveData(config)
        }
    }

    fun onResume() {
        destroyed.value = false
    }

    fun onDestroy() {
        destroyed.value = true
    }

    fun loadArticle(id: Int) {
        viewModelScope.launch {
            article.value = articleRepository.findArticleById(id)
        }
    }

}