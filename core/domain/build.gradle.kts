// core/domain/build.gradle.kts
plugins {
    id("expensetracker.android.library")
    id("expensetracker.android.hilt")
}

android {
    namespace = "com.sundram.expense_tracker.domain"

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.bundles.testing)
    testImplementation(libs.junit5.engine)
}
