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

    implementation("androidx.core:core-ktx:1.10.1")

    // AppCompat
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Material Design
    implementation("com.google.android.material:material:1.9.0")

    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Fragment
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // Activity
    implementation("androidx.activity:activity-ktx:1.7.2")

    // ViewModel & LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // DrawerLayout
    implementation("androidx.drawerlayout:drawerlayout:1.1.1")

    // Navigation Component (可选)
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.2")


    // Room Database (可选)
    implementation("androidx.room:room-runtime:2.5.2")
    annotationProcessor("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")

    // Preference
    implementation("androidx.preference:preference-ktx:1.2.0")

    // Legacy Support (如果你还在用旧布局如 ListView 等)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("org.json:json:20210307")
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
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

}