package ru.eecode.poems

import android.app.Application
import androidx.activity.viewModels
import com.android.billingclient.api.*
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.eecode.poems.domain.StoreViewModel
import ru.eecode.poems.ui.observers.BillingClientLifecycle
import javax.inject.Inject

@HiltAndroidApp
class App @Inject constructor(): Application() {

}