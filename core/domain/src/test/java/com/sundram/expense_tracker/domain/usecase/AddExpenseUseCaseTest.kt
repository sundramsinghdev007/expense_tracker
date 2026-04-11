// core/domain/src/test/java/com/sundram/expense_tracker/domain/usecase/AddExpenseUseCaseTest.kt
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
class AddExpenseUseCaseTest {

    @io.mockk.MockK
    lateinit var repository: ExpenseRepository

    private lateinit var useCase: AddExpenseUseCase

    @BeforeEach
    fun setUp() {
        useCase = AddExpenseUseCase(repository)
    }

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
