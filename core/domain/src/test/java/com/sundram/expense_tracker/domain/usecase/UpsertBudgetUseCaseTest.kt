// core/domain/src/test/java/com/sundram/expense_tracker/domain/usecase/UpsertBudgetUseCaseTest.kt
package com.sundram.expense_tracker.domain.usecase

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.YearMonth
import com.sundram.expense_tracker.domain.model.Budget
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.repository.BudgetRepository

class UpsertBudgetUseCaseTest {

    private val repository: BudgetRepository = mockk()
    private val useCase = UpsertBudgetUseCase(repository)

    @Test
    fun `invoke delegates to BudgetRepository`() = runTest {
        val budget = Budget(
            id = 1L,
            category = Category.FOOD,
            limitAmount = 5000.0,
            spentAmount = 1200.0,
            month = YearMonth.now()
        )
        coEvery { repository.upsertBudget(budget) } just Runs

        useCase(budget)

        coVerify(exactly = 1) { repository.upsertBudget(budget) }
    }
}
