package ru.eecode.poems.domain.store

import android.graphics.drawable.Drawable
import com.android.billingclient.api.SkuDetails

class StoreProduct constructor(var skuDetails: SkuDetails) {
    var isPurchased: Boolean = false
    var image: Drawable? = null
}