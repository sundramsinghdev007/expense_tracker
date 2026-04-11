// core/domain/src/test/java/com/sundram/expense_tracker/domain/usecase/AddExpenseUseCaseTest.kt
package com.sundram.expense_tracker.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.domain.repository.ExpenseRepository

class AddExpenseUseCaseTest {

    private val repository: ExpenseRepository = mockk()
    private val useCase = AddExpenseUseCase(repository)

    @Test
    fun `invoke emits success with valid expense`() = runTest {
        val expense = Expense(
            title = "Coffee",
            amount = 150.0,
            category = Category.FOOD,
            date = LocalDate.now()
        )
        coEvery { repository.insert(expense) } returns 1L

        val result = useCase(expense)

        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
    }

    @Test
    fun `invoke returns failure when title is blank`() = runTest {
        val expense = Expense(
            title = "",
            amount = 150.0,
            category = Category.FOOD,
            date = LocalDate.now()
        )

        val result = useCase(expense)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.insert(any()) }
    }

    @Test
    fun `invoke returns failure when amount is zero`() = runTest {
        val expense = Expense(
            title = "Coffee",
            amount = 0.0,
            category = Category.FOOD,
            date = LocalDate.now()
        )

        val result = useCase(expense)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.insert(any()) }
    }

    @Test
    fun `invoke returns failure when amount is negative`() = runTest {
        val expense = Expense(
            title = "Coffee",
            amount = -10.0,
            category = Category.FOOD,
            date = LocalDate.now()
        )

        val result = useCase(expense)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.insert(any()) }
    }
}
