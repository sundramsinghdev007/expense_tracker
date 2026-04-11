// core/data/build.gradle.kts
plugins {
    id("expensetracker.android.library")
    id("expensetracker.android.hilt")
}

android {
    namespace = "com.sundram.expense_tracker.data"
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)
    implementation(libs.kotlinx.coroutines.android)

    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
