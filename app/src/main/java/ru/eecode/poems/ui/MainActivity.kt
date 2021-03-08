package ru.eecode.poems.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.drawerlayout.widget.DrawerLayout

import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import ru.eecode.poems.R
import ru.eecode.poems.domain.AdsViewModel
import ru.eecode.poems.domain.StoreViewModel
import ru.eecode.poems.domain.store.StoreProduct
import ru.eecode.poems.ui.observers.AdsLifecycle
import ru.eecode.poems.ui.observers.BillingClientLifecycle
import ru.eecode.poems.utils.storeImageFinder
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration: AppBarConfiguration

    lateinit var navController: NavController

    private val storeViewModel: StoreViewModel by viewModels()

    private val adsViewModel: AdsViewModel by viewModels()

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
        navView.itemIconTintList = null

        val crashButton = Button(this)
        crashButton.text = "Crash!"
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        addContentView(crashButton, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT))

        resProducts = resources.getStringArray(R.array.products)

        lifecycle.addObserver(billingClientLifecycle)

        val adsLifecycle = AdsLifecycle(this, findViewById(R.id.bannerContainer))
        lifecycle.addObserver(adsLifecycle)

        billingClientLifecycle.purchaseUpdateEvent.observe(this, {
            storeViewModel.purchases.postValue(it)

            for (purchase in it) {
                if (resProducts.contains(purchase.sku)) {
                    adsViewModel.disableAds()
                    break
                } else {
                    adsViewModel.emitShowBannerEvent()
                }
            }
        })

        billingClientLifecycle.skus.observe(this, { it ->
            storeViewModel.products.postValue(it.map { item ->
                StoreProduct(item).let {
                    it.title = resources.getString(R.string.store_item_title)
                    it.image = storeImageFinder(this, it.skuDetails.sku)
                    it
                }
            })
        })

        storeViewModel.buyEvent.observe(this, {
            it?.let { billingClientLifecycle.launchBillingFlow(this, it) }
        })


        adsViewModel.loadInterstitialEvent.observe( this, {
            if (it) adsLifecycle.loadInterstitialAd()
        })

        adsViewModel.showInterstitialEvent.observe(this, {
            if (it) adsLifecycle.showInterstitialId()
        })

        adsViewModel.showBannerEvent.observe(this, {
            if (it) adsLifecycle.showBanner() else adsLifecycle.hideBanner()
        })
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

}