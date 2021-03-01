package ru.eecode.poems.domain.store

import com.android.billingclient.api.SkuDetails

class StoreProduct constructor(var skuDetails: SkuDetails) {
    var isPurchased: Boolean = false
}