// feature/settings/build.gradle.kts
plugins {
    id("expensetracker.android.feature")
}

android {
    namespace = "com.sundram.expense_tracker.settings"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(libs.androidx.datastore.preferences)
}
