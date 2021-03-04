package ru.eecode.poems.ui.observers

import android.app.Activity
import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.eecode.poems.ui.SingleLiveEvent

class BillingClientLifecycle constructor(
    private val app: Application,
    private val listOfSkus: List<String>
) : LifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener,
    SkuDetailsResponseListener {

    val purchaseUpdateEvent = SingleLiveEvent<List<Purchase>>()

    private val purchases = MutableLiveData<List<Purchase>>()

    val skus = MutableLiveData<List<SkuDetails>>()

    private lateinit var billingClient: BillingClient

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        billingClient = BillingClient.newBuilder(app.applicationContext)
            .setListener(this)
            .enablePendingPurchases() // Not used for subscriptions.
            .build()

        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode

        if (responseCode == BillingClient.BillingResponseCode.OK) {
            querySkuDetails()
            queryPurchases()
        }
    }

    override fun onBillingServiceDisconnected() {
        // TODO: Try connecting again with exponential backoff.
    }

    private fun querySkuDetails() {
        val params = SkuDetailsParams.newBuilder()
            .setType(BillingClient.SkuType.INAPP)
            .setSkusList(listOfSkus)
            .build()
        params.let { skuDetailsParams ->
            billingClient.querySkuDetailsAsync(skuDetailsParams, this)
        }
    }

    override fun onSkuDetailsResponse(
        billingResult: BillingResult,
        skuDetailsList: MutableList<SkuDetails>?
    ) {

        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (skuDetailsList == null) {
                    skus.postValue(emptyList())
                } else {
                    skuDetailsList.let { skus.postValue(it) }
                }
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
            BillingClient.BillingResponseCode.ERROR -> {

            }
            BillingClient.BillingResponseCode.USER_CANCELED,
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {

            }
        }
    }

    private fun queryPurchases() {
        val result = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        processPurchases(result.purchasesList)
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                processPurchases(purchases)
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {

            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {

            }
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {

            }
        }
    }


    private fun processPurchases(purchasesList: List<Purchase>?) {
        if (isUnchangedPurchaseList(purchasesList)) {
            return
        }

        purchasesList?.let {
            purchaseUpdateEvent.postValue(it)
            purchases.postValue(it)
        }

        purchasesList?.let {
            GlobalScope.launch (Dispatchers.IO) {
                for (purchase in it) {
                    acknowledgePurchase(purchase.purchaseToken)
                }
            }
        }
    }

    private fun isUnchangedPurchaseList(purchasesList: List<Purchase>?): Boolean {
        // TODO: Optimize to avoid updates with identical data.
        return false
    }

    fun launchBillingFlow(activity: Activity, params: BillingFlowParams): Int {
        val billingResult = billingClient.launchBillingFlow(activity, params)
        return billingResult.responseCode
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient.acknowledgePurchase(params) { billingResult ->

        }
    }

}