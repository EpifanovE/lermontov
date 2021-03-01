package ru.eecode.poems.ui.observers

import android.util.Log
import android.view.Gravity
import android.widget.Toast
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
import ru.eecode.poems.domain.store.StoreProduct
import ru.eecode.poems.ui.MainActivity

class MainActivityStoreObserver constructor(
    val activity: MainActivity,
    val storeViewModel: StoreViewModel,
    val onBillingClientCreated: (billingClient: BillingClient) -> Unit
) : LifecycleObserver {

    lateinit var billingClient: BillingClient

    var resProducts: Array<String> = activity.resources.getStringArray(R.array.products)

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {

        storeViewModel.productsInResources.value = resProducts

        billingClient = BillingClient.newBuilder(activity)
            .setListener { billingResult, purchases ->
                activity.lifecycleScope.launch {
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
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    activity.lifecycleScope.launch {
                        var purchases: Purchase.PurchasesResult
                        withContext(Dispatchers.IO) {
                            purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
                            querySkuDetails()
                        }
                        storeViewModel.purchases.value = purchases.purchasesList
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // TODO: Implement
            }
        })
    }

    fun querySkuDetails() {
        val skuList = ArrayList<String>()
        skuList.addAll(resProducts)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (skuDetailsList != null) {
                storeViewModel.setProducts(skuDetailsList.map {
                    StoreProduct(it)
                })
            }

        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                val ackPurchaseResult = withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) {
                        if (it.responseCode == BillingClient.BillingResponseCode.OK) {
                            storeViewModel.addPurchases(arrayListOf(purchase))
                            showToast(activity.resources.getString(R.string.thanks_for_purchase))
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 20)
        toast.show()
    }

}