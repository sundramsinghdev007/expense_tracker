// core/domain/src/main/java/com/sundram/expense_tracker/domain/model/Category.kt
package com.sundram.expense_tracker.domain.model

enum class Category(val displayName: String, val emoji: String) {
    FOOD("Food", "🍔"),
    TRANSPORT("Transport", "🚌"),
    SHOPPING("Shopping", "🛍️"),
    HEALTH("Health", "🏥"),
    UTILITIES("Utilities", "💡"),
    ENTERTAINMENT("Entertainment", "🎬"),
    OTHER("Other", "📦"),
}
