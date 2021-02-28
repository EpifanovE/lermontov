package ru.eecode.poems.ui.observers

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.eecode.poems.R
import ru.eecode.poems.domain.StoreViewModel
import ru.eecode.poems.ui.MainActivity

class MainActivityStoreObserver constructor(
    val activity: MainActivity,
    val storeViewModel: StoreViewModel,
    val onBillingClientCreated: (billingClient: BillingClient) -> Unit
) : LifecycleObserver {

    lateinit var billingClient: BillingClient

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {
        billingClient = BillingClient.newBuilder(activity)
            .setListener { billingResult, purchases ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (purchases != null) {
                        for (purchase in purchases) {
                            handlePurchase(purchase)
                        }
                    }
                }
            }
            .enablePendingPurchases()
            .build()

        onBillingClientCreated(billingClient)

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {

                    activity.lifecycleScope.launch {
                        querySkuDetails()
                        val purchases: Purchase.PurchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
                        storeViewModel.purchases.value = purchases.purchasesList
                    }
                }
            }
            override fun onBillingServiceDisconnected() {
                // TODO: Implement
            }
        })
    }

    suspend fun querySkuDetails() {
        val skuList = ArrayList<String>()
        val resArray = activity.resources.getStringArray(R.array.products)
        skuList.addAll(resArray)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        withContext(Dispatchers.IO) {
            billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
                storeViewModel.products.value = skuDetailsList
            }
        }
    }

    suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                val ackPurchaseResult = withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) {
                        // TODO: Implement
                    }
                }
            }
        }
    }

}