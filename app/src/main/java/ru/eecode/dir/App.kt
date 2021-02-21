package ru.eecode.dir

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App @Inject constructor(): Application() {
    private var mContext: App? = null

    override fun onCreate() {
        super.onCreate()
        mContext = this
    }

    fun getContext(): App? {
        return mContext
    }
}