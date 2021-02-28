package ru.eecode.poems.domain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(): ViewModel() {

    var products: MutableLiveData<List<SkuDetails>> = MutableLiveData(null)

    var purchases: MutableLiveData<List<Purchase>?> = MutableLiveData(null)

    var noAdsPurchased: MutableLiveData<Boolean> = MutableLiveData(false)

    var purchasedAreLoaded: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        purchases.observeForever {
            purchasedAreLoaded.value = true

            if (purchases.value?.size?.compareTo(0) != 0) {
                noAdsPurchased.value = true
            }
        }
    }

}