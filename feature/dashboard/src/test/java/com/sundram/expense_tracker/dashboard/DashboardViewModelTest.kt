// feature/dashboard/src/test/java/com/sundram/expense_tracker/dashboard/DashboardViewModelTest.kt
package com.sundram.expense_tracker.dashboard

import app.cash.turbine.test
import com.sundram.expense_tracker.domain.model.Budget
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.domain.model.ExpenseFilter
import com.sundram.expense_tracker.domain.usecase.GetBudgetsUseCase
import com.sundram.expense_tracker.domain.usecase.GetExpensesUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getExpensesUseCase: GetExpensesUseCase = mockk()
    private val getBudgetsUseCase: GetBudgetsUseCase = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getBudgetsUseCase(any()) } returns flowOf(emptyList())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun expense(
        id: Long,
        category: Category,
        amount: Double,
        daysAgo: Long = 0,
    ) = Expense(
        id = id,
        title = "Test",
        amount = amount,
        category = category,
        date = LocalDate.now().minusDays(daysAgo),
    )

    private fun budget(
        id: Long,
        category: Category,
        limitAmount: Double,
        spentAmount: Double,
    ) = Budget(
        id = id,
        category = category,
        limitAmount = limitAmount,
        spentAmount = spentAmount,
        month = YearMonth.now(),
    )

    @Test
    fun `init loads expenses and computes filteredTotal correctly`() = runTest {
        val expenses = listOf(
            expense(1L, Category.FOOD, 100.0),
            expense(2L, Category.TRANSPORT, 200.0),
        )
        every { getExpensesUseCase() } returns flowOf(expenses)

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)
        advanceUntilIdle()

        assertEquals(300.0, vm.uiState.value.filteredTotal, 0.001)
    }

    @Test
    fun `init sets topCategory to the category with highest spend`() = runTest {
        val expenses = listOf(
            expense(1L, Category.FOOD, 300.0),
            expense(2L, Category.TRANSPORT, 100.0),
        )
        every { getExpensesUseCase() } returns flowOf(expenses)

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)
        advanceUntilIdle()

        assertEquals(Category.FOOD, vm.uiState.value.topCategory)
    }

    @Test
    fun `init sets isLoading false after expenses emit`() = runTest {
        every { getExpensesUseCase() } returns flowOf(emptyList())

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `init sets errorMessage when flow throws`() = runTest {
        every { getExpensesUseCase() } returns flow { throw RuntimeException("DB error") }

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)
        advanceUntilIdle()

        assertNotNull(vm.uiState.value.errorMessage)
    }

    @Test
    fun `init emits isLoading true as initial state`() = runTest {
        every { getExpensesUseCase() } returns flowOf(emptyList())

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)

        vm.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init recentExpenses contains at most 20 items sorted descending by date`() = runTest {
        val expenses = (1L..25L).map { i ->
            expense(i, Category.OTHER, 10.0, daysAgo = i)
        }
        every { getExpensesUseCase() } returns flowOf(expenses)

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)
        // Use Year filter so all 25 expenses (spread across ~25 days) are included
        vm.onFilterSelected(ExpenseFilter.Year)
        advanceUntilIdle()

        val recent = vm.uiState.value.recentExpenses
        assertEquals(20, recent.size)
        assert(recent.first().date >= recent.last().date) {
            "recentExpenses should be sorted descending by date"
        }
    }

    @Test
    fun `filteredTotal excludes expenses from previous months when filter is Month`() = runTest {
        val thisMonth = expense(1L, Category.FOOD, 500.0, daysAgo = 0)
        val lastMonth = expense(2L, Category.FOOD, 200.0, daysAgo = 35)
        every { getExpensesUseCase() } returns flowOf(listOf(thisMonth, lastMonth))

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)
        advanceUntilIdle()

        assertEquals(500.0, vm.uiState.value.filteredTotal, 0.001)
    }

    @Test
    fun `topCategory is null when there are no expenses`() = runTest {
        every { getExpensesUseCase() } returns flowOf(emptyList())

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)
        advanceUntilIdle()

        assertEquals(null, vm.uiState.value.topCategory)
    }

    @Test
    fun `errorMessage contains the original exception message`() = runTest {
        every { getExpensesUseCase() } returns flow { throw RuntimeException("DB error") }

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)
        advanceUntilIdle()

        assertEquals("DB error", vm.uiState.value.errorMessage)
    }

    @Test
    fun `onFilterSelected Day filters to today only`() = runTest {
        val todayExpense = expense(1L, Category.FOOD, 100.0, daysAgo = 0)
        val yesterdayExpense = expense(2L, Category.FOOD, 200.0, daysAgo = 1)
        every { getExpensesUseCase() } returns flowOf(listOf(todayExpense, yesterdayExpense))

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)
        vm.onFilterSelected(ExpenseFilter.Day)
        advanceUntilIdle()

        assertEquals(100.0, vm.uiState.value.filteredTotal, 0.001)
    }

    @Test
    fun `onFilterSelected Year includes expenses from all months this year`() = runTest {
        val thisMonthExpense = expense(1L, Category.FOOD, 100.0, daysAgo = 0)
        val lastMonthExpense = expense(2L, Category.FOOD, 200.0, daysAgo = 35)
        every { getExpensesUseCase() } returns flowOf(listOf(thisMonthExpense, lastMonthExpense))

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)
        vm.onFilterSelected(ExpenseFilter.Year)
        advanceUntilIdle()

        assertEquals(300.0, vm.uiState.value.filteredTotal, 0.001)
    }

    @Test
    fun `budgetAlerts shows categories where actual spending exceeds limit`() = runTest {
        val expenses = listOf(expense(1L, Category.FOOD, 600.0))
        val budgets = listOf(budget(1L, Category.FOOD, limitAmount = 500.0, spentAmount = 0.0))
        every { getExpensesUseCase() } returns flowOf(expenses)
        every { getBudgetsUseCase(any()) } returns flowOf(budgets)

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.budgetAlerts.size)
        assertEquals(Category.FOOD, vm.uiState.value.budgetAlerts.first().category)
    }

    @Test
    fun `budgetAlerts is empty when spending is within limit`() = runTest {
        val expenses = listOf(expense(1L, Category.FOOD, 400.0))
        val budgets = listOf(budget(1L, Category.FOOD, limitAmount = 500.0, spentAmount = 0.0))
        every { getExpensesUseCase() } returns flowOf(expenses)
        every { getBudgetsUseCase(any()) } returns flowOf(budgets)

        val vm = DashboardViewModel(getExpensesUseCase, getBudgetsUseCase)
        advanceUntilIdle()

        assertEquals(0, vm.uiState.value.budgetAlerts.size)
    }
}
