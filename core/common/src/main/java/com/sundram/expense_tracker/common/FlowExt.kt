// core/common/src/main/java/com/sundram/expense_tracker/common/FlowExt.kt
package com.sundram.expense_tracker.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.asResult(): Flow<AppResult<T>> = this
    .map<T, AppResult<T>> { AppResult.Success(it) }
    .onStart { emit(AppResult.Loading) }
    .catch { emit(AppResult.Error(it)) }
