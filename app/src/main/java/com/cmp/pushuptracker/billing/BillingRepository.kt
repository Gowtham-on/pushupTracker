package com.cmp.pushuptracker.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val _premiumState = MutableStateFlow(PremiumState())
    val premiumState: StateFlow<PremiumState> = _premiumState

    private val _productDetails = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetails: StateFlow<List<ProductDetails>> = _productDetails

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun startConnection() {
        if (billingClient.isReady) {
            scope.launch { refreshProductsAndPurchases() }
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                // Try to reconnect in the background.
                scope.launch {
                    kotlin.runCatching {
                        startConnection()
                    }
                }
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    scope.launch { refreshProductsAndPurchases() }
                } else {
                    Log.w(TAG, "Billing setup failed: ${billingResult.debugMessage}")
                }
            }
        })
    }

    private suspend fun refreshProductsAndPurchases() {
        _isLoading.emit(true)
        kotlin.runCatching {
            queryProductDetails()
            queryActivePurchases()
        }.onFailure { throwable ->
            Log.e(TAG, "Failed to refresh billing data", throwable)
        }
        _isLoading.emit(false)
    }

    private suspend fun queryProductDetails() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(BillingProducts.queryProducts)
            .build()
        val productDetailsResult = billingClient.queryProductDetails(params)
        if (productDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            _productDetails.emit(productDetailsResult.productDetailsList ?: emptyList())
        } else {
            Log.w(TAG, "Product details query failed: ${productDetailsResult.billingResult.debugMessage}")
            _productDetails.emit(emptyList())
        }
    }

    private suspend fun queryActivePurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        val purchaseResult = billingClient.queryPurchasesAsync(params)
        if (purchaseResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            handlePurchases(purchaseResult.purchasesList)
        } else {
            Log.w(TAG, "Purchase query failed: ${purchaseResult.billingResult.debugMessage}")
            _premiumState.emit(PremiumState(PremiumStatus.NOT_ENTITLED))
        }
    }

    fun launchBillingFlow(activity: Activity, plan: PaywallPlan) {
        if (plan.isPlaceholder) {
            Log.w(TAG, "Attempted to purchase placeholder plan. Configure real product IDs in Play Console.")
            return
        }
        val product = _productDetails.value.firstOrNull { it.productId == plan.productId } ?: return
        val offerDetails = product.subscriptionOfferDetails?.firstOrNull { it.offerToken == plan.offerToken }
            ?: product.subscriptionOfferDetails?.firstOrNull()
            ?: return

        val params = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(product)
            .setOfferToken(offerDetails.offerToken)
            .build()

        val billingParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(params))
            .build()

        billingClient.launchBillingFlow(activity, billingParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            scope.launch { handlePurchases(purchases) }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "Purchase canceled by user")
        } else if (billingResult.responseCode != BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Log.w(TAG, "Purchase failed: ${billingResult.debugMessage}")
        } else {
            scope.launch { queryActivePurchases() }
        }
    }

    private suspend fun handlePurchases(purchases: List<Purchase>) {
        var hasActive = false
        purchases.filter { purchase ->
            purchase.products.any { BillingProducts.allProductIds.contains(it) }
        }.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                hasActive = true
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }
                _premiumState.emit(
                    PremiumState(
                        status = PremiumStatus.ACTIVE,
                        activePurchaseToken = purchase.purchaseToken
                    )
                )
            }
        }
        if (!hasActive) {
            _premiumState.emit(PremiumState(PremiumStatus.NOT_ENTITLED))
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "Failed to acknowledge purchase: ${billingResult.debugMessage}")
            }
        }
    }

    fun plans(): List<PaywallPlan> {
        val product = _productDetails.value.firstOrNull()
        val plans = product?.subscriptionOfferDetails
            ?.groupBy { it.basePlanId }
            ?.mapNotNull { (_, offers) ->
                val primary = offers.firstOrNull { offer ->
                    offer.pricingPhases.pricingPhaseList.any { it.priceAmountMicros == 0L }
                } ?: offers.firstOrNull()

                primary?.let { offer ->
                    val paidPhases = offer.pricingPhases.pricingPhaseList.filter { it.priceAmountMicros > 0L }
                    val basePhase = paidPhases.lastOrNull()
                    val introPhase = if (paidPhases.size > 1) paidPhases.firstOrNull() else null
                    val displayPhase = introPhase ?: basePhase
                    val info = BillingProducts.infoFor(offer.basePlanId)

                    val compareAt = if (introPhase != null && basePhase != null && introPhase.formattedPrice != basePhase.formattedPrice) {
                        basePhase.formattedPrice
                    } else null

                    val introMessage = introPhase?.billingPeriod?.let { period ->
                        val desc = SubscriptionPeriodFormatter.describe(period)
                        if (desc.isNotBlank()) "Intro price for $desc" else null
                    }

                    PaywallPlan(
                        productId = product.productId,
                        title = info?.title ?: product.title,
                        formattedPrice = displayPhase?.formattedPrice ?: info?.fallbackPrice.orEmpty(),
                        billingPeriod = info?.billingPeriod ?: SubscriptionPeriodFormatter.describe(basePhase?.billingPeriod),
                        freeTrialMessage = SubscriptionPeriodFormatter.trialMessage(offer),
                        offerToken = offer.offerToken,
                        basePlanId = offer.basePlanId,
                        compareAtPrice = compareAt,
                        introOfferMessage = introMessage
                    )
                }
            }
            ?.sortedWith(compareBy { plan -> BillingProducts.sortOrder(plan.basePlanId) })
            ?: emptyList()

        return if (plans.isNotEmpty()) plans else BillingProducts.placeholderPlans()
    }

    fun tearDown() {
        billingClient.endConnection()
    }

    companion object {
        private const val TAG = "BillingRepository"
    }
}
