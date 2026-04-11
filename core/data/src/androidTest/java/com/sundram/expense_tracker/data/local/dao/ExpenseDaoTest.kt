// core/data/src/androidTest/java/com/sundram/expense_tracker/data/local/dao/ExpenseDaoTest.kt
package com.sundram.expense_tracker.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sundram.expense_tracker.data.local.ExpenseTrackerDatabase
import com.sundram.expense_tracker.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExpenseDaoTest {

    private lateinit var database: ExpenseTrackerDatabase
    private lateinit var dao: ExpenseDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ExpenseTrackerDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.expenseDao()
    }

    @After
    fun tearDown() { database.close() }

    private fun entity(
        id: Long = 0,
        title: String = "Test",
        amount: Double = 100.0,
        category: String = "FOOD",
        date: String = "2024-06-15",
    ) = ExpenseEntity(id = id, title = title, amount = amount, category = category, date = date)

    @Test
    fun insertExpense_returnsGeneratedId() = runTest {
        val id = dao.insert(entity())
        assertTrue(id > 0)
    }

    @Test
    fun getAllExpenses_emitsInsertedExpense() = runTest {
        val inserted = entity(title = "Coffee", amount = 50.0)
        dao.insert(inserted)
        val results = dao.getAllExpenses().first()
        assertEquals(1, results.size)
        assertEquals("Coffee", results.first().title)
    }

    @Test
    fun getByCategory_returnsOnlyMatchingCategory() = runTest {
        dao.insert(entity(title = "Coffee", category = "FOOD"))
        dao.insert(entity(title = "Bus", category = "TRANSPORT"))
        val results = dao.getByCategory("FOOD").first()
        assertEquals(1, results.size)
        assertEquals("FOOD", results.first().category)
    }

    @Test
    fun getByDateRange_returnsExpensesWithinRange() = runTest {
        dao.insert(entity(title = "Early", date = "2024-01-01"))
        dao.insert(entity(title = "Mid",   date = "2024-06-15"))
        dao.insert(entity(title = "Late",  date = "2024-12-31"))
        val results = dao.getByDateRange("2024-06-01", "2024-06-30").first()
        assertEquals(1, results.size)
        assertEquals("Mid", results.first().title)
    }

    @Test
    fun deleteExpense_removesItFromGetAllExpenses() = runTest {
        val id = dao.insert(entity(title = "Delete me"))
        val inserted = dao.getAllExpenses().first().first()
        dao.delete(inserted)
        val results = dao.getAllExpenses().first()
        assertTrue(results.isEmpty())
    }

    @Test
    fun updateExpense_reflectsNewValuesInGetAllExpenses() = runTest {
        val id = dao.insert(entity(title = "Original", amount = 100.0))
        val inserted = dao.getById(id)!!
        dao.update(inserted.copy(title = "Updated", amount = 200.0))
        val updated = dao.getAllExpenses().first().first()
        assertEquals("Updated", updated.title)
        assertEquals(200.0, updated.amount, 0.001)
    }
}
