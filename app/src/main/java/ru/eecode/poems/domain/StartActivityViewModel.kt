package ru.eecode.poems.domain

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.eecode.poems.R
import ru.eecode.poems.repository.ArticleRepository
import javax.inject.Inject

@HiltViewModel
class StartActivityViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    application: Application
) : AndroidViewModel(application) {

    var isReady: MutableLiveData<Boolean> = MutableLiveData(false)

    private val contentInDbVersion: Int

    private val newContentVersion: Int

    init {
        val prefs: SharedPreferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        contentInDbVersion = prefs.getInt("content_version", 0)
        newContentVersion = application.resources.getInteger(R.integer.contentVersion)

        viewModelScope.launch {

            val count = articleRepository.checkDbContent()

            if (checkForContentUpdate(count)) {
                articleRepository.articlesSeed()

                with(prefs.edit()) {
                    putInt("content_version", newContentVersion)
                    apply()
                }
            }

            isReady.value = true
        }
    }

    private fun checkForContentUpdate(count: Int) : Boolean {
        if (count == 0) {
            return true
        }

        return contentInDbVersion < newContentVersion
    }
}