plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
//    id("com.google.devtools.ksp")
}

android {
    namespace = "com.nudge.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    val room_version = "2.6.1"
    implementation(project(":internetSpeedChecker"))

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    implementation("com.google.code.gson:gson:2.9.0")

    implementation("com.google.dagger:hilt-android:2.43.2")
    kapt("com.google.dagger:hilt-android-compiler:2.43.2")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    //Firebase Firestore
    implementation("com.google.firebase:firebase-firestore")
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")

    //CSV Export
    implementation ("com.opencsv:opencsv:5.3")
    implementation("androidx.compose.ui:ui:1.4.1")
//Material
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material:1.3.1")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("com.github.wwdablu:SimplyPDF:2.0.0")

}