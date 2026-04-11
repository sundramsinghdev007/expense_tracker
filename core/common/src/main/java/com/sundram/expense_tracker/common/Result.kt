// core/common/src/main/java/com/sundram/expense_tracker/common/Result.kt
package com.sundram.expense_tracker.common

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val exception: Throwable) : AppResult<Nothing>()
    object Loading : AppResult<Nothing>()
}
