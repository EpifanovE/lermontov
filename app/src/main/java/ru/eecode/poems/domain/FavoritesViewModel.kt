package ru.eecode.poems.domain

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.eecode.poems.repository.ArticleRepository
import ru.eecode.poems.repository.db.articles.ArticleListItem
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel  @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    var favorites: LiveData<PagedList<ArticleListItem>>

    init {
        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(20)
            .setPageSize(20)
            .build()

        favorites = articleRepository.loadFavorites().toLiveData(config)
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

    fun clearFavorites() {
        viewModelScope.launch {
            articleRepository.clearFavorites()
        }
    }
}