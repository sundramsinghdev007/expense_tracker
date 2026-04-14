// feature/add-expense/src/test/java/com/sundram/expense_tracker/addexpense/AddExpenseViewModelTest.kt
package com.sundram.expense_tracker.addexpense

import app.cash.turbine.test
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.usecase.AddExpenseUseCase
import com.sundram.expense_tracker.domain.usecase.CheckBudgetAfterExpenseUseCase
import com.sundram.expense_tracker.domain.usecase.DeleteExpenseUseCase
import com.sundram.expense_tracker.domain.usecase.GetExpenseByIdUseCase
import com.sundram.expense_tracker.domain.usecase.UpdateExpenseUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AddExpenseViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val addExpenseUseCase: AddExpenseUseCase = mockk()
    private val checkBudgetAfterExpenseUseCase: CheckBudgetAfterExpenseUseCase = mockk()
    private val getExpenseByIdUseCase: GetExpenseByIdUseCase = mockk()
    private val updateExpenseUseCase: UpdateExpenseUseCase = mockk()
    private val deleteExpenseUseCase: DeleteExpenseUseCase = mockk()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { checkBudgetAfterExpenseUseCase(any()) } returns false
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createVm() = AddExpenseViewModel(
        addExpenseUseCase,
        checkBudgetAfterExpenseUseCase,
        getExpenseByIdUseCase,
        updateExpenseUseCase,
        deleteExpenseUseCase,
    )

    // -------------------------------------------------------------------------
    // Initial state
    // -------------------------------------------------------------------------

    @Test
    fun `initial uiState has empty title and amount`() = runTest {
        val vm = createVm()
        assertEquals("", vm.uiState.value.title)
        assertEquals("", vm.uiState.value.amount)
    }

    @Test
    fun `initial uiState has isSaved false and isLoading false`() = runTest {
        val vm = createVm()
        assertFalse(vm.uiState.value.isSaved)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `initial uiState selectedCategory defaults to OTHER`() = runTest {
        val vm = createVm()
        assertEquals(Category.OTHER, vm.uiState.value.selectedCategory)
    }

    // -------------------------------------------------------------------------
    // Field mutation
    // -------------------------------------------------------------------------

    @Test
    fun `onTitleChange updates uiState title`() = runTest {
        val vm = createVm()
        vm.onTitleChange("Lunch")
        assertEquals("Lunch", vm.uiState.value.title)
    }

    @Test
    fun `onAmountChange updates uiState amount`() = runTest {
        val vm = createVm()
        vm.onAmountChange("250.0")
        assertEquals("250.0", vm.uiState.value.amount)
    }

    @Test
    fun `onCategoryChange updates uiState selectedCategory`() = runTest {
        val vm = createVm()
        vm.onCategoryChange(Category.FOOD)
        assertEquals(Category.FOOD, vm.uiState.value.selectedCategory)
    }

    @Test
    fun `onDateChange updates uiState date`() = runTest {
        val vm = createVm()
        val newDate = LocalDate.of(2026, 3, 15)
        vm.onDateChange(newDate)
        assertEquals(newDate, vm.uiState.value.date)
    }

    @Test
    fun `onNotesChange updates uiState notes`() = runTest {
        val vm = createVm()
        vm.onNotesChange("Work lunch")
        assertEquals("Work lunch", vm.uiState.value.notes)
    }

    // -------------------------------------------------------------------------
    // saveExpense — success path
    // -------------------------------------------------------------------------

    @Test
    fun `saveExpense emits isSaved true on use case success`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.success(1L)
        val vm = createVm()
        vm.onTitleChange("Coffee")
        vm.onAmountChange("150.0")

        vm.saveExpense()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isSaved)
    }

    @Test
    fun `saveExpense emits NavigateBack event on success`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.success(1L)
        val vm = createVm()
        vm.onTitleChange("Coffee")
        vm.onAmountChange("150.0")

        vm.events.test {
            vm.saveExpense()
            advanceUntilIdle()
            assertEquals(AddExpenseUiEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveExpense sets isLoading false after successful save`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.success(1L)
        val vm = createVm()
        vm.onTitleChange("Coffee")
        vm.onAmountChange("150.0")

        vm.saveExpense()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `saveExpense emits BudgetExceeded then NavigateBack when budget is exceeded`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.success(1L)
        coEvery { checkBudgetAfterExpenseUseCase(any()) } returns true
        val vm = createVm()
        vm.onTitleChange("Coffee")
        vm.onAmountChange("150.0")

        vm.events.test {
            vm.saveExpense()
            advanceUntilIdle()
            assertTrue(awaitItem() is AddExpenseUiEvent.BudgetExceeded)
            assertEquals(AddExpenseUiEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // saveExpense — failure path
    // -------------------------------------------------------------------------

    @Test
    fun `saveExpense emits ShowSnackbar on use case failure`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.failure(RuntimeException("Insert failed"))
        val vm = createVm()
        vm.onTitleChange("Coffee")
        vm.onAmountChange("150.0")

        vm.events.test {
            vm.saveExpense()
            advanceUntilIdle()
            assertTrue(awaitItem() is AddExpenseUiEvent.ShowSnackbar)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveExpense ShowSnackbar carries a non-zero resource id on failure`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.failure(RuntimeException("Insert failed"))
        val vm = createVm()
        vm.onTitleChange("Coffee")
        vm.onAmountChange("150.0")

        vm.events.test {
            vm.saveExpense()
            advanceUntilIdle()
            val event = awaitItem() as AddExpenseUiEvent.ShowSnackbar
            assertNotEquals(0, event.messageRes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveExpense sets isLoading false after use case failure`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.failure(RuntimeException("Insert failed"))
        val vm = createVm()
        vm.onTitleChange("Coffee")
        vm.onAmountChange("150.0")

        vm.saveExpense()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
    }

    // -------------------------------------------------------------------------
    // saveExpense — validation: blank title
    // -------------------------------------------------------------------------

    @Test
    fun `saveExpense does not call use case when title is blank`() = runTest {
        val vm = createVm()
        vm.onTitleChange("")
        vm.onAmountChange("100.0")

        vm.saveExpense()
        advanceUntilIdle()

        coVerify(exactly = 0) { addExpenseUseCase(any()) }
    }

    @Test
    fun `saveExpense stores title validation error when title is blank`() = runTest {
        val vm = createVm()
        vm.onTitleChange("")
        vm.onAmountChange("100.0")

        vm.saveExpense()

        assertTrue(vm.uiState.value.validationErrors.containsKey("title"))
    }

    @Test
    fun `saveExpense does not emit NavigateBack when title is blank`() = runTest {
        val vm = createVm()
        vm.onTitleChange("")
        vm.onAmountChange("100.0")

        vm.events.test {
            vm.saveExpense()
            advanceUntilIdle()
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // -------------------------------------------------------------------------
    // saveExpense — validation: invalid amount
    // -------------------------------------------------------------------------

    @Test
    fun `saveExpense does not call use case when amount is not a number`() = runTest {
        val vm = createVm()
        vm.onTitleChange("Coffee")
        vm.onAmountChange("abc")

        vm.saveExpense()
        advanceUntilIdle()

        coVerify(exactly = 0) { addExpenseUseCase(any()) }
    }

    @Test
    fun `saveExpense does not call use case when amount is zero`() = runTest {
        val vm = createVm()
        vm.onTitleChange("Coffee")
        vm.onAmountChange("0.0")

        vm.saveExpense()
        advanceUntilIdle()

        coVerify(exactly = 0) { addExpenseUseCase(any()) }
    }

    @Test
    fun `saveExpense does not call use case when amount is negative`() = runTest {
        val vm = createVm()
        vm.onTitleChange("Coffee")
        vm.onAmountChange("-10.0")

        vm.saveExpense()
        advanceUntilIdle()

        coVerify(exactly = 0) { addExpenseUseCase(any()) }
    }

    @Test
    fun `saveExpense stores amount validation error when amount is invalid`() = runTest {
        val vm = createVm()
        vm.onTitleChange("Coffee")
        vm.onAmountChange("not-a-number")

        vm.saveExpense()

        assertTrue(vm.uiState.value.validationErrors.containsKey("amount"))
    }

    // -------------------------------------------------------------------------
    // saveExpense — passes trimmed title to use case
    // -------------------------------------------------------------------------

    @Test
    fun `saveExpense passes trimmed title to use case`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.success(1L)
        val vm = createVm()
        vm.onTitleChange("  Coffee  ")
        vm.onAmountChange("50.0")

        vm.saveExpense()
        advanceUntilIdle()

        coVerify {
            addExpenseUseCase(match { it.title == "Coffee" })
        }
    }
}
