package ru.eecode.poems.domain

import javax.inject.Inject

data class StoreViewModelConfig @Inject constructor(
    val interstitialCount: Int,
    val isPaidVersion: Boolean
)