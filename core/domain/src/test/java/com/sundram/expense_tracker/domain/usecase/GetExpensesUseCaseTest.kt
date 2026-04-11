// core/domain/src/test/java/com/sundram/expense_tracker/domain/usecase/GetExpensesUseCaseTest.kt
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
class GetExpensesUseCaseTest {

    @io.mockk.MockK
    lateinit var repository: ExpenseRepository

    private lateinit var useCase: GetExpensesUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetExpensesUseCase(repository)
    }

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
