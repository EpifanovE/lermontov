package ru.eecode.poems.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout

import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import ru.eecode.poems.R
import ru.eecode.poems.domain.AdsViewModel
import ru.eecode.poems.domain.StoreViewModel
import ru.eecode.poems.domain.store.StoreProduct
import ru.eecode.poems.ui.observers.AdsLifecycle
import ru.eecode.poems.ui.observers.BillingClientLifecycle
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var navController: NavController

    val storeViewModel: StoreViewModel by viewModels()

    val adsViewModel: AdsViewModel by viewModels()

    lateinit var resProducts: Array<String>

    @Inject
    lateinit var billingClientLifecycle: BillingClientLifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_articles
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        resProducts = resources.getStringArray(R.array.products)
        storeViewModel.productsInResources.value = resProducts

        lifecycle.addObserver(billingClientLifecycle)

        billingClientLifecycle.purchaseUpdateEvent.observe(this, {
            Log.d("my_log_", "Purchases: ${it}")
            storeViewModel.purchases.postValue(it)
        })

        billingClientLifecycle.skus.observe(this, {
            storeViewModel.products.postValue(it.map { item -> StoreProduct(item) })
        })

        storeViewModel.buyEvent.observe(this, {
            it?.let { billingClientLifecycle.launchBillingFlow(this, it) }
        })

        val adsLifecycle = AdsLifecycle(this, findViewById(R.id.bannerContainer))
        lifecycle.addObserver(adsLifecycle)

        adsViewModel.loadInterstitialEvent.observe( this, {
            if (it) adsLifecycle.loadInterstitialAd()
        })

        adsViewModel.showInterstitialEvent.observe(this, { it ->
            if (it) adsLifecycle.showInterstitialId()
        })

        adsViewModel.showBannerEvent.observe(this, {
            if (it) adsLifecycle.showBanner()
        })

        adsViewModel.emitShowBannerEvent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu);
        val settingsItem: MenuItem = menu.findItem(R.id.action_settings)
        settingsItem.isVisible = navController.currentDestination?.id != R.id.nav_settings
        return true;
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                navController.navigate(R.id.nav_settings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}