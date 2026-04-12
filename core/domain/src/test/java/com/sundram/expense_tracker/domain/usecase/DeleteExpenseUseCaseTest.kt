// core/domain/src/test/java/com/sundram/expense_tracker/domain/usecase/DeleteExpenseUseCaseTest.kt
package com.sundram.expense_tracker.domain.usecase

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.LocalDate
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.domain.repository.ExpenseRepository

class DeleteExpenseUseCaseTest {

    private val repository: ExpenseRepository = mockk()
    private val useCase = DeleteExpenseUseCase(repository)

    @Test
    fun `invoke calls repository delete with correct expense`() = runTest {
        val expense = Expense(
            id = 1L,
            title = "Coffee",
            amount = 50.0,
            category = Category.FOOD,
            date = LocalDate.now()
        )
        coEvery { repository.delete(expense) } just Runs

        useCase(expense)

        coVerify(exactly = 1) { repository.delete(expense) }
    }
}
