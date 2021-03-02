package ru.eecode.poems.domain

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.BillingFlowParams
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.eecode.poems.ui.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class AdsViewModel @Inject constructor(
    val interstitialCount: Int
) : ViewModel() {

    val loadEvent = SingleLiveEvent<Boolean>()

    val showEvent = SingleLiveEvent<Boolean>()

}