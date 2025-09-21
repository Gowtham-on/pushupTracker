package com.cmp.pushuptracker.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.QueryProductDetailsParams

/**
 * Central place for all subscription identifiers configured in Play Console.
 * Update these IDs to match the products/base plans you create in the console.
 */
object BillingProducts {
    const val PRODUCT_ID = "pushup_plus"

    data class BasePlanInfo(
        val basePlanId: String,
        val title: String,
        val fallbackPrice: String,
        val billingPeriod: String,
        val displayOrder: Int
    )

    private val basePlanInfos = listOf(
        BasePlanInfo("pushup-plus-1m", "1 Month", "$3 / month", "1 month", 0),
        BasePlanInfo("pushup-plus-3m", "3 Months", "$7 / 3 months", "3 months", 1),
        BasePlanInfo("pushup-plus-6m", "6 Months", "$12 / 6 months", "6 months", 2),
        BasePlanInfo("pushup-plus-12m", "12 Months", "$20 / year", "12 months", 3)
    )

    val allProductIds = listOf(PRODUCT_ID)

    val queryProducts: List<QueryProductDetailsParams.Product> = listOf(
        QueryProductDetailsParams.Product.newBuilder()
            .setProductId(PRODUCT_ID)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
    )

    fun infoFor(basePlanId: String?): BasePlanInfo? = basePlanInfos.firstOrNull { it.basePlanId == basePlanId }

    fun sortOrder(basePlanId: String?): Int = infoFor(basePlanId)?.displayOrder ?: Int.MAX_VALUE

    fun placeholderPlans(): List<PaywallPlan> = basePlanInfos.map {
        PaywallPlan(
            productId = PRODUCT_ID,
            title = it.title,
            formattedPrice = it.fallbackPrice,
            billingPeriod = it.billingPeriod,
            freeTrialMessage = "Free trial included",
            offerToken = it.basePlanId,
            basePlanId = it.basePlanId,
            compareAtPrice = null,
            introOfferMessage = null,
            isPlaceholder = true
        )
    }
}
