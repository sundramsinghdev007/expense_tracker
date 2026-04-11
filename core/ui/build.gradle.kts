// core/ui/build.gradle.kts
plugins {
    id("expensetracker.android.feature")
}
android { namespace = "com.sundram.expense_tracker.ui" }
dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.bundles.compose)
    api(libs.androidx.navigation.compose)
    api(libs.androidx.hilt.navigation.compose)
}
