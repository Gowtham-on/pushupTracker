package com.cmp.pushuptracker.ui.screen.paywall

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cmp.pushuptracker.billing.BillingViewModel
import com.cmp.pushuptracker.billing.PaywallPlan
import com.cmp.pushuptracker.billing.PremiumStatus
import com.cmp.pushuptracker.ui.components.AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    navController: NavHostController,
    billingViewModel: BillingViewModel = hiltViewModel()
) {
    val uiState by billingViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showContent by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.premiumState.status) {
        if (uiState.premiumState.status == PremiumStatus.ACTIVE) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.loading) {
        if (!uiState.loading) {
            showContent = true
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        AppBar("Pushup Plus") { navController.popBackStack() }

        if (uiState.loading && uiState.plans.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(
                animationSpec = spring(
                    dampingRatio = 0.85f,
                    stiffness = Spring.StiffnessLow
                )
            ) +
                    slideInVertically(initialOffsetY = { it / 4 }),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Start your free trial",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Unlock guided workouts and quick logging tools. Cancel anytime during the trial.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(uiState.plans) { plan ->
                    PlanCard(plan = plan) {
                        val activity = context as? Activity ?: return@PlanCard
                        billingViewModel.launchBilling(activity, plan)
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Purchases are handled by Google Play and can be managed in the Play Store under Subscriptions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanCard(
    plan: PaywallPlan,
    onPurchase: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        var animate by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { animate = true }
        val cardScale by animateFloatAsState(
            targetValue = if (animate) 1f else 0.94f,
            animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
            label = "cardScale"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(scaleX = cardScale, scaleY = cardScale)
                .padding(20.dp)
        ) {
            Text(plan.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedVisibility(
                    visible = plan.compareAtPrice != null,
                    enter = fadeIn(
                        animationSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) +
                            scaleIn(
                                initialScale = 1.1f,
                                animationSpec = spring(
                                    dampingRatio = 0.8f,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ),
                    exit = fadeOut() + scaleOut(targetScale = 0.9f)
                ) {
                    Text(
                        text = plan.compareAtPrice ?: "",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                }
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(
                        animationSpec = spring(
                            dampingRatio = 0.7f,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    val priceScale by animateFloatAsState(
                        targetValue = if (animate) 1f else 0.9f,
                        animationSpec = spring(
                            dampingRatio = 0.65f,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "priceScale"
                    )
                    Text(
                        plan.formattedPrice,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .graphicsLayer(scaleX = priceScale, scaleY = priceScale)
                            .padding(start = if (plan.compareAtPrice != null) 8.dp else 0.dp)
                    )
                }
            }
            if (plan.billingPeriod.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Billed every ${plan.billingPeriod}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            plan.freeTrialMessage?.let {
                Spacer(Modifier.height(6.dp))
                Text(text = it, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            }
            plan.introOfferMessage?.let {
                Spacer(Modifier.height(4.dp))
                Text(text = it, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onPurchase,
                modifier = Modifier.fillMaxWidth(),
                enabled = !plan.isPlaceholder
            ) {
                Text(text = if (plan.isPlaceholder) "Configure Billing" else "Continue")
            }
            if (plan.isPlaceholder) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Set up matching subscription products in Play Console to enable purchasing.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
