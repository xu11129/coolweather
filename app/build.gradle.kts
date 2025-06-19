plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.coolweather"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.coolweather"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(fileTree("libs") { include("*.jar") })
    testImplementation("junit:junit:4.12")
    implementation("com.squareup.okhttp3:okhttp:3.4.1")
    implementation("com.google.code.gson:gson:2.7")
    implementation("com.github.bumptech.glide:glide:3.7.0")
    implementation(files("libs/litepal-2.0.0.jar"))


}