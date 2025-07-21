plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.application.glamessence"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.application.glamessence"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.google.firebase:firebase-auth:22.3.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.cloudinary:cloudinary-android:2.3.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
