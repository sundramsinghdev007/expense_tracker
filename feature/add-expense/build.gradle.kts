// feature/add-expense/build.gradle.kts
plugins {
    id("expensetracker.android.feature")
}

android {
    namespace = "com.sundram.expense_tracker.addexpense"
    testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    testImplementation(libs.bundles.testing)
    testImplementation(libs.junit5.engine)
}
