// feature/dashboard/src/androidTest/java/com/sundram/expense_tracker/dashboard/DashboardContentTest.kt
package com.sundram.expense_tracker.dashboard

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.ui.theme.ExpenseTrackerTheme
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class DashboardContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(
        uiState: DashboardUiState,
        onAddExpense: () -> Unit = {},
        onExpenseClick: (Long) -> Unit = {},
    ) = composeTestRule.setContent {
        ExpenseTrackerTheme {
            DashboardContent(
                uiState = uiState,
                onAddExpense = onAddExpense,
                onScanReceipt = {},
                onExpenseClick = onExpenseClick,
            )
        }
    }

    @Test
    fun shows_LoadingIndicator_when_isLoading_is_true() {
        setContent(DashboardUiState(isLoading = true))
        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertExists()
    }

    @Test
    fun shows_EmptyState_when_expenses_list_is_empty() {
        setContent(DashboardUiState(isLoading = false, recentExpenses = emptyList()))
        composeTestRule
            .onNodeWithText("No expenses yet. Tap + to add one!")
            .assertIsDisplayed()
    }

    @Test
    fun shows_SummaryHeader_with_correct_total_amount() {
        setContent(
            DashboardUiState(
                isLoading = false,
                totalThisMonth = 1250.0,
                recentExpenses = listOf(
                    Expense(1L, "Coffee", 50.0, Category.FOOD, LocalDate.now()),
                ),
            )
        )
        composeTestRule.onNodeWithText("₹1250.00").assertIsDisplayed()
    }

    @Test
    fun renders_one_ExpenseCard_per_expense_in_list() {
        val expenses = listOf(
            Expense(1L, "Coffee", 50.0, Category.FOOD, LocalDate.now()),
            Expense(2L, "Bus ticket", 20.0, Category.TRANSPORT, LocalDate.now()),
        )
        setContent(DashboardUiState(isLoading = false, recentExpenses = expenses))
        composeTestRule.onNodeWithText("Coffee").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bus ticket").assertIsDisplayed()
    }

    @Test
    fun ExpenseCard_displays_correct_title_and_amount() {
        val expenses = listOf(Expense(1L, "Lunch", 150.0, Category.FOOD, LocalDate.now()))
        setContent(DashboardUiState(isLoading = false, recentExpenses = expenses))
        composeTestRule.onNodeWithText("Lunch").assertIsDisplayed()
        composeTestRule.onNodeWithText("₹150.00").assertIsDisplayed()
    }

    @Test
    fun tapping_ExpenseCard_triggers_onExpenseClick_with_correct_id() {
        var clickedId = -1L
        val expenses = listOf(Expense(42L, "Gym", 500.0, Category.HEALTH, LocalDate.now()))
        setContent(
            uiState = DashboardUiState(isLoading = false, recentExpenses = expenses),
            onExpenseClick = { clickedId = it },
        )
        composeTestRule.onNodeWithText("Gym").performClick()
        assert(clickedId == 42L)
    }

    @Test
    fun FAB_click_triggers_onAddExpense() {
        var addClicked = false
        setContent(
            uiState = DashboardUiState(isLoading = false),
            onAddExpense = { addClicked = true },
        )
        composeTestRule.onNodeWithContentDescription("Add expense").performClick()
        assert(addClicked)
    }
}
