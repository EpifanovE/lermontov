package ru.eecode.poems.domain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.eecode.poems.ui.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class AdsViewModel @Inject constructor(
    private val config: StoreViewModelConfig
) : ViewModel() {

    val loadInterstitialEvent = SingleLiveEvent<Boolean>()

    val showInterstitialEvent = SingleLiveEvent<Boolean>()

    val showBannerEvent = SingleLiveEvent<Boolean>()

    private val adsIsDisabled = MutableLiveData(false)

    var count: Int = 0

    fun emitLoadInterstitialEvent() {
        if (!isAdsActive()) {
            return
        }

        if (count < config.interstitialCount - 1) {
            count++
            return
        }

        loadInterstitialEvent.postValue(true)
        count = 0

    }

    fun emitShowInterstitialEvent() {
        if (isAdsActive()) {
            showInterstitialEvent.postValue(true)
        }
    }

    fun emitShowBannerEvent() {
        if (isAdsActive()) {
            showBannerEvent.postValue(true)
        }
    }

    fun disableAds() {
        adsIsDisabled.postValue(true)
        showBannerEvent.postValue(false)
    }

    private fun isAdsActive(): Boolean {
        if (config.isPaidVersion) {
            return false
        }

        if (adsIsDisabled.value == true || adsIsDisabled.value == null) {
            return false
        }

        return true
    }

}