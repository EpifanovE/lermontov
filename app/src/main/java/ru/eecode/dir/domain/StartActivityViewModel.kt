package ru.eecode.dir.domain

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.eecode.dir.R
import ru.eecode.dir.repository.ArticleRepository

class StartActivityViewModel @ViewModelInject constructor(
    private val articleRepository: ArticleRepository,
    application: Application
) : AndroidViewModel(application) {

    var isReady: MutableLiveData<Boolean> = MutableLiveData(false)

    private val contentInDbVersion: Int

    private val newContentVersion: Int

    init {
        val prefs: SharedPreferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        contentInDbVersion = prefs.getInt("content_version", 0)
        newContentVersion = application.resources.getInteger(R.integer.content_version)

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