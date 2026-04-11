// core/common/build.gradle.kts
plugins {
    id("expensetracker.android.library")
}
android { namespace = "com.sundram.expense_tracker.common" }
dependencies {
    implementation(libs.kotlinx.coroutines.android)
}
