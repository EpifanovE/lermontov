package ru.eecode.poems.ui.observers

import android.util.DisplayMetrics
import android.widget.RelativeLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.ads.*
import ru.eecode.poems.R
import ru.eecode.poems.ui.MainActivity

class AdsLifecycle constructor(
    private val activity: MainActivity,
    private val adViewContainer: RelativeLayout,
) : LifecycleObserver {

    private val interstitialAd = InterstitialAd(activity.applicationContext)

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {
        MobileAds.initialize(activity) {}
        interstitialAd.adUnitId = activity.getString(R.string.interstitialAdId)
    }

    fun showBanner() {

        var adView = AdView(activity)

        adViewContainer.addView(adView)

        adView.let {
            it.adUnitId = activity.resources.getString(R.string.bannerAdId)
            it.adSize = adSize

            val adRequest = AdRequest
                .Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()

            it.loadAd(adRequest)
        }
    }

    fun showInterstitialId() {
        if (interstitialAd.isLoaded) interstitialAd.show()
    }

    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        interstitialAd.loadAd(adRequest)
    }

    private val adSize: AdSize
        get() {
            val display = activity.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = adViewContainer.width.toFloat()

            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
        }
}