// core/domain/src/test/java/com/sundram/expense_tracker/domain/usecase/UpsertBudgetUseCaseTest.kt
package com.sundram.expense_tracker.domain.usecase

import app.cash.turbine.test
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.YearMonth
import com.sundram.expense_tracker.domain.model.*
import com.sundram.expense_tracker.domain.repository.*
import com.sundram.expense_tracker.domain.usecase.*

@ExtendWith(MockKExtension::class)
class UpsertBudgetUseCaseTest {

    @io.mockk.MockK
    lateinit var repository: BudgetRepository

    private lateinit var useCase: UpsertBudgetUseCase

    @BeforeEach
    fun setUp() {
        useCase = UpsertBudgetUseCase(repository)
    }

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
