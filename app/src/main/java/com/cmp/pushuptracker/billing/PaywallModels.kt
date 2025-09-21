package com.cmp.pushuptracker.billing

import com.android.billingclient.api.ProductDetails

data class PaywallPlan(
    val productId: String,
    val title: String,
    val formattedPrice: String,
    val billingPeriod: String,
    val freeTrialMessage: String?,
    val offerToken: String,
    val basePlanId: String?,
    val compareAtPrice: String? = null,
    val introOfferMessage: String? = null,
    val isPlaceholder: Boolean = false
)

data class BillingUiState(
    val loading: Boolean = true,
    val plans: List<PaywallPlan> = emptyList(),
    val premiumState: PremiumState = PremiumState(),
    val errorMessage: String? = null
)
