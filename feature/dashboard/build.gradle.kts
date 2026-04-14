// feature/dashboard/build.gradle.kts
plugins {
    id("expensetracker.android.feature")
}

android {
    namespace = "com.sundram.expense_tracker.dashboard"
    testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit5.engine)
    testRuntimeOnly(libs.junit5.launcher)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
