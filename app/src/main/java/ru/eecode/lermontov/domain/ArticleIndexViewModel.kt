package ru.eecode.lermontov.domain

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.launch
import ru.eecode.lermontov.repository.ArticleRepository
import ru.eecode.lermontov.repository.db.articles.ArticleListItem

class ArticleIndexViewModel @ViewModelInject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    var articles: LiveData<PagedList<ArticleListItem>>

    var filter: MutableLiveData<String?> = MutableLiveData(null)

    var destroyed: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
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

    fun addToFavorites(id: Int) {
        viewModelScope.launch {
            articleRepository.addToFavorites(id)
        }
    }

    fun removeFromFavorites(id: Int) {
        viewModelScope.launch {
            articleRepository.removeFromFavorites(id)
        }
    }

}