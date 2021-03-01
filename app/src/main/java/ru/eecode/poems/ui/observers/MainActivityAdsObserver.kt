package ru.eecode.poems.ui.observers

import android.content.Context
import android.content.SharedPreferences
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import ru.eecode.poems.R
import ru.eecode.poems.ui.MainActivity

class MainActivityAdsObserver constructor(
    val activity: MainActivity,
    private val adViewContainer: RelativeLayout,
    private val navController: NavController
) : LifecycleObserver {

    private lateinit var adView: AdView

    private var interstitialAd: InterstitialAd? = null

    private lateinit var prefs: SharedPreferences

    private var interstitialNumber : Int = 5

    private val interstitialCountKey: String = "interstitial_count"

    private var enableAds: Boolean = false

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {

        interstitialNumber = activity.resources.getInteger(R.integer.interstitialNumber)
        prefs = activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        MobileAds.initialize(activity) {}

        navController.addOnDestinationChangedListener { _, destination, _ -> onNavDestinationChange(destination) }

        activity.storeViewModel.noAdsPurchased.observe(activity, {
            if (it == true) {
                disableAds()
            } else if (it == false){
                enableAds()
            }
        });
    }

    private fun enableAds() {
        if (activity.resources.getBoolean(R.bool.isPaidVersion)) {
            return;
        }
        enableAds = true
        initBanner()
    }

    private fun initBanner() {
        adView = AdView(activity)
        adViewContainer.addView(adView)

        adView.adUnitId = activity.resources.getString(R.string.bannerAdId)
        adView.adSize = adSize

        val adRequest = AdRequest
            .Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()

        adView.loadAd(adRequest)
    }

    private fun disableAds() {
        enableAds = false
        adViewContainer.removeAllViews()
        adViewContainer.visibility = View.GONE
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


    private fun loadInterstitialAd() {

        if (!enableAds) {
            return
        }

        if (prefs.getInt(interstitialCountKey, 0) < interstitialNumber - 1) {
            return
        }

        InterstitialAd.load(
            activity,
            activity.resources.getString(R.string.interstitialAdId),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad

                    interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                            interstitialAd = null

                            with (prefs.edit()) {
                                putInt(interstitialCountKey, 0)
                                apply()
                            }
                        }

                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            interstitialAd = null
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError?) {
                            super.onAdFailedToShowFullScreenContent(p0)
                            interstitialAd = null
                        }
                    }
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    interstitialAd = null
                }
            }
        )
    }

    private fun showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd?.show(activity)
        }
    }

    private fun onNavDestinationChange(destination: NavDestination) {
        if (destination.id == R.id.nav_articles || destination.id == R.id.nav_favorites) {
            showInterstitialAd()
        }

        if (destination.id == R.id.nav_article) {
            with (prefs.edit()) {
                var currentCount = prefs.getInt(interstitialCountKey, 0);
                putInt(interstitialCountKey, ++currentCount)
                apply()
            }
            loadInterstitialAd()
        }

        if (destination.id == R.id.nav_store || destination.id == R.id.nav_settings) {
            adViewContainer.visibility = View.GONE
        } else {
            adViewContainer.visibility = View.VISIBLE
        }
    }
}
