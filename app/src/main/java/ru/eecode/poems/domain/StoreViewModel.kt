package ru.eecode.poems.domain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.eecode.poems.domain.store.StoreProduct
import ru.eecode.poems.ui.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor() : ViewModel() {

    var products: MutableLiveData<List<StoreProduct>> = MutableLiveData(ArrayList())

    var purchases: MutableLiveData<List<Purchase>> = MutableLiveData(ArrayList())

    var noAdsPurchased: MutableLiveData<Boolean> = MutableLiveData(false)

    var purchasedAreLoaded: MutableLiveData<Boolean> = MutableLiveData(false)

    var productsInResources: MutableLiveData<Array<String>> = MutableLiveData(null)

    val buyEvent = SingleLiveEvent<BillingFlowParams>()

    init {
        purchases.observeForever {
            purchasedAreLoaded.value = true

//            if (isProductAvailable(it)) {
//                noAdsPurchased.value = true
//            }

            products.value?.let { products -> setProducts(products) }
        }
    }

    private fun isProductAvailable(purchases: List<Purchase>?): Boolean {
        if (productsInResources.value.isNullOrEmpty()) {
            return false
        }

        if (purchases.isNullOrEmpty()) {
            return false
        }

        for (purchase in purchases) {
            if (productsInResources.value?.contains(purchase.sku) == true) {
                return true
            }
        }

        return false
    }

    fun addPurchases(purchasesToAdd: List<Purchase>?) {
        val newPurchases = arrayListOf<Purchase>()

        purchasesToAdd?.let { newPurchases.addAll(it) }
        purchases.value?.let { newPurchases.addAll(it) }

        purchases.value = newPurchases
    }

    fun setProducts(productsToSet: List<StoreProduct>) {
        products.value = productsToSet.map {
            it.isPurchased = isPurchased(it.skuDetails)
            it
        }
    }

    private fun isPurchased(skuDetails: SkuDetails): Boolean {
        val purchasesSkus = purchases.value?.map {
            it.sku
        }

        return purchasesSkus?.contains(skuDetails.sku) == true
    }

    fun buy(skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        buyEvent.postValue(flowParams)
    }

}