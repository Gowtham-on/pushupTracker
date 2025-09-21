package com.cmp.pushuptracker.billing

data class PremiumState(
    val status: PremiumStatus = PremiumStatus.UNKNOWN,
    val activePurchaseToken: String? = null
) {
    val isEntitled: Boolean get() = status == PremiumStatus.ACTIVE
}

enum class PremiumStatus {
    UNKNOWN,
    NOT_ENTITLED,
    ACTIVE
}
