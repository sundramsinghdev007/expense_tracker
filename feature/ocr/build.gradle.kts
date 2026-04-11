// feature/ocr/build.gradle.kts
plugins {
    id("expensetracker.android.feature")
}

android {
    namespace = "com.sundram.expense_tracker.ocr"

    testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(libs.bundles.cameraX)
    implementation(libs.mlkit.text.recognition)

    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
    testImplementation(libs.junit5.params)
}
