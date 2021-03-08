package ru.eecode.poems.domain

import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.eecode.poems.repository.ArticleRepository
import ru.eecode.poems.db.model.ArticleListItem
import javax.inject.Inject

@HiltViewModel
class ArticleIndexViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    var articles: LiveData<PagedList<ArticleListItem>>

    var filter: MutableLiveData<String?> = MutableLiveData(null)

    var filterChanged: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .build()

        articles = Transformations.switchMap(filter) {
            articleRepository.loadArticles(it).toLiveData(config)
        }

        filter.observeForever {
            filterChanged.value = true
        }
    }

    fun onDestroy() {
        filterChanged.value = false
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

    fun needToResetPosition() : Boolean {
        return filterChanged.value == true
    }

}