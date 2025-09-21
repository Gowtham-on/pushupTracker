package com.cmp.pushuptracker.billing

import java.time.Period
import java.util.Locale

object SubscriptionPeriodFormatter {
    fun describe(billingPeriod: String?): String {
        if (billingPeriod.isNullOrBlank()) return ""
        return try {
            if (billingPeriod.contains("W")) {
                val weeks = billingPeriod.removePrefix("P").removeSuffix("W").toIntOrNull()
                if (weeks != null) {
                    return pluralize(weeks, "week")
                }
            }
            val period = Period.parse(billingPeriod)
            when {
                period.years > 0 -> pluralize(period.years, "year")
                period.months > 0 -> pluralize(period.months, "month")
                period.days > 0 -> pluralize(period.days, "day")
                else -> billingPeriod
            }
        } catch (e: Exception) {
            billingPeriod
        }
    }

    fun trialMessage(offerDetails: com.android.billingclient.api.ProductDetails.SubscriptionOfferDetails?): String? {
        val trialPhase = offerDetails?.pricingPhases?.pricingPhaseList?.firstOrNull { it.priceAmountMicros == 0L }
        return trialPhase?.billingPeriod?.let { period ->
            val duration = describe(period)
            if (duration.isNotBlank()) {
                "Free trial for $duration"
            } else null
        }
    }

    fun pricePhaseDescription(offerDetails: com.android.billingclient.api.ProductDetails.SubscriptionOfferDetails?): String {
        val paidPhase = offerDetails?.pricingPhases?.pricingPhaseList?.firstOrNull { it.priceAmountMicros > 0L }
        val duration = describe(paidPhase?.billingPeriod)
        val price = paidPhase?.formattedPrice ?: ""
        return listOfNotNull(price, if (duration.isNotBlank()) "per $duration" else null)
            .joinToString(" ")
            .ifBlank { price }
    }

    private fun pluralize(value: Int, unit: String): String {
        val normalized = if (value <= 0) 1 else value
        val suffix = if (normalized == 1) unit else unit + "s"
        return String.format(Locale.getDefault(), "%d %s", normalized, suffix)
    }
}
