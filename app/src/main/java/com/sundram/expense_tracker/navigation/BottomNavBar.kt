// app/src/main/java/com/sundram/expense_tracker/navigation/BottomNavBar.kt
package com.sundram.expense_tracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sundram.expense_tracker.R

private data class NavItem(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
    val contentDescRes: Int,
)

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem(AppRoutes.DASHBOARD, R.string.nav_dashboard, Icons.Filled.Home,     R.string.nav_dashboard_cd),
        NavItem(AppRoutes.ANALYTICS, R.string.nav_analytics, Icons.Filled.BarChart, R.string.nav_analytics_cd),
        NavItem(AppRoutes.BUDGETS,   R.string.nav_budgets,   Icons.Filled.Wallet,   R.string.nav_budgets_cd),
        NavItem(AppRoutes.SETTINGS,  R.string.nav_settings,  Icons.Filled.Settings, R.string.nav_settings_cd),
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick  = {
                    navController.navigate(item.route) {
                        popUpTo(AppRoutes.DASHBOARD) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon  = { Icon(item.icon, contentDescription = stringResource(item.contentDescRes)) },
                label = { Text(stringResource(item.labelRes)) },
            )
        }
    }
}
