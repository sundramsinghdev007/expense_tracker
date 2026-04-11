// app/build.gradle.kts
plugins {
    id("expensetracker.android.application")
    id("expensetracker.android.hilt")
}

android {
    namespace = "com.sundram.expense_tracker"
    defaultConfig {
        applicationId = "com.sundram.expense_tracker"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:add-expense"))
    implementation(project(":feature:analytics"))
    implementation(project(":feature:ocr"))
    implementation(project(":feature:budgets"))
    implementation(project(":feature:settings"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
}
