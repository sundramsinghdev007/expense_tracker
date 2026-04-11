// feature/add-expense/src/test/java/com/sundram/expense_tracker/addexpense/AddExpenseViewModelTest.kt
package com.sundram.expense_tracker.addexpense

import app.cash.turbine.test
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.domain.usecase.AddExpenseUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit5.MockKExtension
import io.mockk.MockK
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockKExtension::class)
class AddExpenseViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @MockK
    lateinit var addExpenseUseCase: AddExpenseUseCase

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // -------------------------------------------------------------------------
    // Initial state
    // -------------------------------------------------------------------------

    @Test
    fun `initial uiState has empty title and amount`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)

        assertEquals("", vm.uiState.value.title)
        assertEquals("", vm.uiState.value.amount)
    }

    @Test
    fun `initial uiState has isSaved false and isLoading false`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)

        assertFalse(vm.uiState.value.isSaved)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `initial uiState selectedCategory defaults to OTHER`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)

        assertEquals(Category.OTHER, vm.uiState.value.selectedCategory)
    }

    // -------------------------------------------------------------------------
    // Field mutation
    // -------------------------------------------------------------------------

    @Test
    fun `onTitleChange updates uiState title`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)

        vm.onTitleChange("Lunch")

        assertEquals("Lunch", vm.uiState.value.title)
    }

    @Test
    fun `onAmountChange updates uiState amount`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)

        vm.onAmountChange("250.0")

        assertEquals("250.0", vm.uiState.value.amount)
    }

    @Test
    fun `onCategoryChange updates uiState selectedCategory`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)

        vm.onCategoryChange(Category.FOOD)

        assertEquals(Category.FOOD, vm.uiState.value.selectedCategory)
    }

    @Test
    fun `onDateChange updates uiState date`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)
        val newDate = LocalDate.of(2026, 3, 15)

        vm.onDateChange(newDate)

        assertEquals(newDate, vm.uiState.value.date)
    }

    @Test
    fun `onNotesChange updates uiState notes`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)

        vm.onNotesChange("Work lunch")

        assertEquals("Work lunch", vm.uiState.value.notes)
    }

    // -------------------------------------------------------------------------
    // saveExpense — success path
    // -------------------------------------------------------------------------

    @Test
    fun `saveExpense emits isSaved true on use case success`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.success(1L)
        val vm = AddExpenseViewModel(addExpenseUseCase)
        vm.onTitleChange("Coffee")
        vm.onAmountChange("150.0")

        vm.saveExpense()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isSaved)
    }

    @Test
    fun `saveExpense emits NavigateBack event on success`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.success(1L)
        val vm = AddExpenseViewModel(addExpenseUseCase)
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
        val vm = AddExpenseViewModel(addExpenseUseCase)
        vm.onTitleChange("Coffee")
        vm.onAmountChange("150.0")

        vm.saveExpense()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
    }

    // -------------------------------------------------------------------------
    // saveExpense — failure path
    // -------------------------------------------------------------------------

    @Test
    fun `saveExpense emits ShowSnackbar on use case failure`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.failure(RuntimeException("Insert failed"))
        val vm = AddExpenseViewModel(addExpenseUseCase)
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
    fun `saveExpense ShowSnackbar contains the exception message`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.failure(RuntimeException("Insert failed"))
        val vm = AddExpenseViewModel(addExpenseUseCase)
        vm.onTitleChange("Coffee")
        vm.onAmountChange("150.0")

        vm.events.test {
            vm.saveExpense()
            advanceUntilIdle()
            val event = awaitItem() as AddExpenseUiEvent.ShowSnackbar
            assertEquals("Insert failed", event.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveExpense sets isLoading false after use case failure`() = runTest {
        coEvery { addExpenseUseCase(any()) } returns Result.failure(RuntimeException("Insert failed"))
        val vm = AddExpenseViewModel(addExpenseUseCase)
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
        val vm = AddExpenseViewModel(addExpenseUseCase)
        vm.onTitleChange("")
        vm.onAmountChange("100.0")

        vm.saveExpense()
        advanceUntilIdle()

        coVerify(exactly = 0) { addExpenseUseCase(any()) }
    }

    @Test
    fun `saveExpense stores title validation error when title is blank`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)
        vm.onTitleChange("")
        vm.onAmountChange("100.0")

        vm.saveExpense()

        assertTrue(vm.uiState.value.validationErrors.containsKey("title"))
    }

    @Test
    fun `saveExpense does not emit NavigateBack when title is blank`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)
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
        val vm = AddExpenseViewModel(addExpenseUseCase)
        vm.onTitleChange("Coffee")
        vm.onAmountChange("abc")

        vm.saveExpense()
        advanceUntilIdle()

        coVerify(exactly = 0) { addExpenseUseCase(any()) }
    }

    @Test
    fun `saveExpense does not call use case when amount is zero`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)
        vm.onTitleChange("Coffee")
        vm.onAmountChange("0.0")

        vm.saveExpense()
        advanceUntilIdle()

        coVerify(exactly = 0) { addExpenseUseCase(any()) }
    }

    @Test
    fun `saveExpense does not call use case when amount is negative`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)
        vm.onTitleChange("Coffee")
        vm.onAmountChange("-10.0")

        vm.saveExpense()
        advanceUntilIdle()

        coVerify(exactly = 0) { addExpenseUseCase(any()) }
    }

    @Test
    fun `saveExpense stores amount validation error when amount is invalid`() = runTest {
        val vm = AddExpenseViewModel(addExpenseUseCase)
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
        val vm = AddExpenseViewModel(addExpenseUseCase)
        vm.onTitleChange("  Coffee  ")
        vm.onAmountChange("50.0")

        vm.saveExpense()
        advanceUntilIdle()

        coVerify {
            addExpenseUseCase(match { it.title == "Coffee" })
        }
    }
}
