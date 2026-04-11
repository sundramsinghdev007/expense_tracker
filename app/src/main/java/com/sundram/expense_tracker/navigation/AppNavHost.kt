// app/src/main/java/com/sundram/expense_tracker/navigation/AppNavHost.kt
package com.sundram.expense_tracker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sundram.expense_tracker.dashboard.DashboardScreen
import com.sundram.expense_tracker.addexpense.AddExpenseScreen
import com.sundram.expense_tracker.analytics.AnalyticsScreen
import com.sundram.expense_tracker.ocr.OcrScanScreen
import com.sundram.expense_tracker.budgets.BudgetsScreen
import com.sundram.expense_tracker.settings.SettingsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = AppRoutes.DASHBOARD,
            modifier         = Modifier.padding(innerPadding),
        ) {
            composable(AppRoutes.DASHBOARD) {
                DashboardScreen(
                    onAddExpense  = { navController.navigate(AppRoutes.ADD_EXPENSE) },
                    onScanReceipt = { navController.navigate(AppRoutes.OCR) },
                )
            }
            composable(AppRoutes.ADD_EXPENSE) {
                AddExpenseScreen(onBack = { navController.popBackStack() })
            }
            composable(AppRoutes.ANALYTICS) {
                AnalyticsScreen()
            }
            composable(AppRoutes.OCR) {
                OcrScanScreen(
                    onNavigateToAddExpense = { _, _, _ ->
                        navController.navigate(AppRoutes.ADD_EXPENSE)
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(AppRoutes.BUDGETS) {
                BudgetsScreen()
            }
            composable(AppRoutes.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}
