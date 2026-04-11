// core/domain/src/test/java/com/sundram/expense_tracker/domain/usecase/GetExpensesUseCaseTest.kt
package com.sundram.expense_tracker.domain.usecase

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.domain.repository.ExpenseRepository

class GetExpensesUseCaseTest {

    private val repository: ExpenseRepository = mockk()
    private val useCase = GetExpensesUseCase(repository)

    @Test
    fun `invoke returns mapped flow from repository`() = runTest {
        val expenses = listOf(
            Expense(
                id = 1L,
                title = "Coffee",
                amount = 50.0,
                category = Category.FOOD,
                date = LocalDate.now()
            )
        )
        every { repository.getAllExpenses() } returns flowOf(expenses)

        useCase().test {
            assertEquals(expenses, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits empty list when repository is empty`() = runTest {
        every { repository.getAllExpenses() } returns flowOf(emptyList())

        useCase().test {
            assertTrue(awaitItem().isEmpty())
            awaitComplete()
        }
    }
}
