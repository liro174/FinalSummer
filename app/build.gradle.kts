plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.finalsummer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.finalsummer"
        minSdk = 24 // המינימום שהגדרת
        targetSdk = 35 // הטארגט שהגדרת
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 🚨 זהו התיקון הקריטי: הוסף את השורה הבאה
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        // מומלץ לוודא שזה מתאים לגרסת ה-JDK שלך ולפרויקט כולו
        sourceCompatibility = JavaVersion.VERSION_1_8 // בדרך כלל 1.8 מספיק, אלא אם אתה משתמש בתכונות חדשות יותר של Java
        targetCompatibility = JavaVersion.VERSION_1_8 // בדרך כלל 1.8 מספיק
    }
}

dependencies {
    // ... other dependencies
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    implementation(libs.recyclerview)
    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}