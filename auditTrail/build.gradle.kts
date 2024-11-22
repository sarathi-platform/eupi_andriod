//plugins {
//    id("com.android.library")
//    id("org.jetbrains.kotlin.android")
//    kotlin("kapt")
//    id("dagger.hilt.android.plugin")
//}
//
//android {
//    namespace = "com.nudge.auditTrail"
//    compileSdk = 34
//
//    defaultConfig {
//        minSdk = 24
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")
//
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
////    buildFeatures {
////        compose = true
////    }
//}
//
//dependencies {
//    val retrofit = "2.9.0"
//    val room_version = "2.6.1"
//    val okhttp3 = "4.9.0"
//    implementation(project(":core"))
//    implementation(project(":internetSpeedChecker"))
//    implementation ("com.squareup.retrofit2:retrofit:$retrofit")
//    implementation ("com.squareup.retrofit2:converter-gson:$retrofit")
//    implementation("androidx.core:core-ktx:1.12.0")
//
//    //Room
//    implementation ("androidx.room:room-runtime:$room_version")
//    implementation ("androidx.room:room-ktx:$room_version")
//    //annotationProcessor("androidx.room:room-compiler:$room_version")
//    kapt ("androidx.room:room-compiler:$room_version")
//   //hilt
//    implementation("com.google.dagger:hilt-android:2.43.2")
//    kapt("com.google.dagger:hilt-android-compiler:2.43.2")
//    kapt("androidx.hilt:hilt-compiler:1.0.0")
////    kapt("androidx.hilt:hilt-compiler:1.0.0")
//    //work
//    implementation ("androidx.hilt:hilt-work:1.0.0")
//    implementation("androidx.work:work-runtime-ktx:2.7.1")
//    implementation("com.google.code.gson:gson:2.9.0")
//    implementation("com.squareup.retrofit2:retrofit:$retrofit")
//    implementation("com.squareup.retrofit2:converter-gson:$retrofit")
//    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp3")
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.2.1")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
//}
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
//    id("com.google.devtools.ksp")
}

android {
    namespace = "com.nudge.auditTrail"
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
}

dependencies {
    val retrofit = "2.9.0"
    val okhttp3 = "4.9.0"
    implementation(project(":core"))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
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

    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.compose.runtime:runtime-livedata:1.4.3")
    implementation("com.mixpanel.android:mixpanel-android:7.+")

    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp3")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-work:1.0.0")


}