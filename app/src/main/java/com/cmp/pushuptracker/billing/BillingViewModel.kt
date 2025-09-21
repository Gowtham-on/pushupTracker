package com.cmp.pushuptracker.billing

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val billingRepository: BillingRepository
) : ViewModel() {

    private val manualError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<BillingUiState> = combine(
        billingRepository.isLoading,
        billingRepository.premiumState,
        billingRepository.productDetails,
        manualError
    ) { loading, premiumState, _, error ->
        BillingUiState(
            loading = loading,
            premiumState = premiumState,
            plans = billingRepository.plans(),
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BillingUiState()
    )

    init {
        billingRepository.startConnection()
    }

    fun launchBilling(activity: Activity, plan: PaywallPlan) {
        manualError.value = null
        billingRepository.launchBillingFlow(activity, plan)
    }

    override fun onCleared() {
        super.onCleared()
        billingRepository.tearDown()
    }
}
