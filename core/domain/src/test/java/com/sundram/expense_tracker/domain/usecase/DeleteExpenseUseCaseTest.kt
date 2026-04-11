// core/domain/src/test/java/com/sundram/expense_tracker/domain/usecase/DeleteExpenseUseCaseTest.kt
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
class DeleteExpenseUseCaseTest {

    @io.mockk.MockK
    lateinit var repository: ExpenseRepository

    private lateinit var useCase: DeleteExpenseUseCase

    @BeforeEach
    fun setUp() {
        useCase = DeleteExpenseUseCase(repository)
    }

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
